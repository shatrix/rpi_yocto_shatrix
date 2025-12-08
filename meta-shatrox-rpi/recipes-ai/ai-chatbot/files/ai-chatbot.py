#!/usr/bin/env python3
"""
AI Chatbot Orchestration Service
Handles voice chat and camera vision with state machine
"""

import os
import sys
import time
import socket
import threading
import subprocess
import configparser
import json
import signal
from pathlib import Path
from datetime import datetime
from enum import Enum
import ollama

# Configuration
CONFIG_FILE = "/etc/ai-chatbot/config.ini"
SOCKET_PATH = "/tmp/ai-chatbot.sock"
LOG_FILE = "/var/log/robot-ai.log"
RECORDINGS_DIR = "/tmp/ai-recordings"
CAMERA_DIR = "/tmp/ai-camera"

class State(Enum):
    IDLE = "idle"
    LISTENING = "listening"
    TRANSCRIBING = "transcribing"
    ANSWERING = "answering"
    SPEAKING = "speaking"
    CAMERA = "camera"

class AIChatBot:
    def __init__(self):
        self.state = State.IDLE
        self.config = self.load_config()
        self.socket_server = None
        self.recording_process = None
        self.current_audio_file = None
        self.conversation_history = []
        self.last_interaction_time = time.time()
        
        # Create directories
        Path(RECORDINGS_DIR).mkdir(parents=True, exist_ok=True)
        Path(CAMERA_DIR).mkdir(parents=True, exist_ok=True)
        
    def load_config(self):
        """Load configuration from INI file"""
        config = configparser.ConfigParser()
        
        # Defaults
        config['llm'] = {
            'system_prompt': 'You are a helpful robot assistant. Answer questions in 2-3 sentences maximum. Be concise, direct, and friendly.',
            'model_path': '/usr/share/llama-models/vision/default',
            'max_tokens': '150',
            'temperature': '0.7',
            'context_size': '2048'
        }
        config['whisper'] = {
            'model': 'base',
            'sample_rate': '16000'
        }
        config['audio'] = {
            'microphone_device': 'plughw:1,0',
            'speaker_device': 'plughw:1,0',
            'sample_rate': '16000'
        }
        config['camera'] = {
            'enable': 'true',
            'resolution': '640x480'
        }
        config['behavior'] = {
            'chat_history_timeout': '300',
            'max_history_messages': '10'
        }
        
        # Load from file if exists
        if os.path.exists(CONFIG_FILE):
            config.read(CONFIG_FILE)
            
        return config
    
    def log(self, message, level="INFO"):
        """Write to log file and stdout"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        log_msg = f"[{timestamp}] [{level}] {message}"
        print(log_msg)
        
        try:
            with open(LOG_FILE, 'a') as f:
                f.write(log_msg + "\n")
        except Exception as e:
            print(f"Failed to write to log: {e}")
    
    def update_display(self, status, text=""):
        """Send status update to QML display via log file"""
        try:
            display_msg = {
                "type": "chat_status",
                "state": status,
                "text": text,
                "timestamp": time.time()
            }
            
            # Write to shared log file (same as button service)
            with open(LOG_FILE, 'a') as f:
                f.write(f"CHAT_STATUS:{json.dumps(display_msg)}\n")
                
        except Exception as e:
            self.log(f"Failed to update display: {e}", "ERROR")
    
    def set_state(self, new_state):
        """Change state and update display"""
        self.log(f"State transition: {self.state.value} -> {new_state.value}")
        self.state = new_state
        self.update_display(new_state.value)
    
    def start_recording(self):
        """Start audio recording"""
        self.set_state(State.LISTENING)
        
        # Generate unique filename
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.current_audio_file = os.path.join(RECORDINGS_DIR, f"recording_{timestamp}.wav")
        
        # Start arecord process
        mic_device = self.config['audio']['microphone_device']
        sample_rate = self.config['audio']['sample_rate']
        
        try:
            self.recording_process = subprocess.Popen([
                'arecord',
                '-D', mic_device,
                '-f', 'S16_LE',
                '-r', sample_rate,
                '-c', '1',
                self.current_audio_file
            ], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            
            self.log(f"Started recording to {self.current_audio_file}")
            self.update_display("listening", "🎤 Listening...")
            
        except Exception as e:
            self.log(f"Failed to start recording: {e}", "ERROR")
            self.set_state(State.IDLE)
    
    def stop_recording(self):
        """Stop audio recording and start transcription"""
        if self.recording_process:
            self.recording_process.terminate()
            self.recording_process.wait(timeout=2)
            self.recording_process = None
            self.log("Stopped recording")
        
        if self.current_audio_file and os.path.exists(self.current_audio_file):
            # Check if file has content
            if os.path.getsize(self.current_audio_file) > 1000:  # At least 1KB
                self.transcribe_audio()
            else:
                self.log("Recording too short, ignoring", "WARN")
                self.set_state(State.IDLE)
        else:
            self.set_state(State.IDLE)
    
    def transcribe_audio(self):
        """Transcribe audio with Whisper"""
        self.set_state(State.TRANSCRIBING)
        self.update_display("transcribing", "🔄 Transcribing...")
        
        try:
            # Run Whisper transcription
            result = subprocess.run([
                'whisper-transcribe',
                self.current_audio_file
            ], capture_output=True, text=True, timeout=30)
            
            transcribed_text = result.stdout.strip()
            
            if transcribed_text:
                self.log(f"Transcribed: {transcribed_text}")
                self.update_display("transcribing", transcribed_text)
                self.answer_question(transcribed_text)
            else:
                self.log("No speech detected", "WARN")
                self.set_state(State.IDLE)
                
        except subprocess.TimeoutExpired:
            self.log("Transcription timeout", "ERROR")
            self.set_state(State.IDLE)
        except Exception as e:
            self.log(f"Transcription failed: {e}", "ERROR")
            self.set_state(State.IDLE)
        finally:
            # Clean up audio file
            if self.current_audio_file and os.path.exists(self.current_audio_file):
                os.remove(self.current_audio_file)
    
    def answer_question(self, question):
        """Get answer from LLM"""
        self.set_state(State.ANSWERING)
        self.update_display("answering", "🤔 Thinking...")
        
        # Check if we should reset history (before updating timestamp)
        timeout = int(self.config['behavior']['chat_history_timeout'])
        if time.time() - self.last_interaction_time > timeout:
            self.log("Resetting conversation history (timeout)")
            self.conversation_history = []
        
        # Update last interaction time
        self.last_interaction_time = time.time()
        
        # Add to conversation history
        self.conversation_history.append({"role": "user", "content": question})
        
        # Limit history
        max_history = int(self.config['behavior']['max_history_messages'])
        if len(self.conversation_history) > max_history:
            self.conversation_history = self.conversation_history[-max_history:]
        
        try:
            # Use Ollama for text chat
            # Model: qwen2.5:1.5b (imported by ollama-models)
            response = ollama.chat(model='qwen2.5:1.5b', messages=[
                {
                    'role': 'system',
                    'content': self.config['llm']['system_prompt']
                },
                {
                    'role': 'user',
                    'content': question
                }
            ])
            
            answer = response['message']['content']
            
            if answer:
                self.log(f"Answer: {answer}")
                self.conversation_history.append({"role": "assistant", "content": answer})
                self.speak_answer(answer)
            else:
                self.log("No answer generated", "WARN")
                self.set_state(State.IDLE)
                
        except Exception as e:
            self.log(f"LLM failed: {e}", "ERROR")
            self.set_state(State.IDLE)
    
    def speak_answer(self, text):
        """Convert text to speech and play"""
        self.set_state(State.SPEAKING)
        self.update_display("speaking", text)
        
        try:
            # Use existing 'speak' command (from your button service)
            subprocess.run(['speak', text], timeout=30)
            
            self.log("Finished speaking")
            self.set_state(State.IDLE)
            
        except subprocess.TimeoutExpired:
            self.log("TTS timeout", "ERROR")
            self.set_state(State.IDLE)
        except Exception as e:
            self.log(f"TTS failed: {e}", "ERROR")
            self.set_state(State.IDLE)
    
    def capture_camera(self):
        """Capture image and describe it"""
        if self.config['camera']['enable'].lower() != 'true':
            self.log("Camera disabled in config", "WARN")
            return
        
        self.set_state(State.CAMERA)
        self.update_display("camera", "📷 Capturing...")
        
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        image_path = os.path.join(CAMERA_DIR, f"capture_{timestamp}.jpg")
        
        try:
            # Capture image with libcamera
            subprocess.run([
                'libcamera-still',
                '-o', image_path,
                '-t', '1000',  # 1 second delay
                '--width', '640',
                '--height', '480',
                '--nopreview'
            ], timeout=10, check=True)
            
            self.log(f"Captured image: {image_path}")
            
            # Describe image with vision model
            self.describe_image(image_path)
            
        except subprocess.TimeoutExpired:
            self.log("Camera capture timeout", "ERROR")
            self.set_state(State.IDLE)
        except Exception as e:
            self.log(f"Camera capture failed: {e}", "ERROR")
            self.set_state(State.IDLE)
    
    def describe_image(self, image_path):
        """Use vision model to describe image"""
        self.set_state(State.ANSWERING)
        self.update_display("answering", "🤔 Analyzing image...")
        
        try:
            # Use Ollama for vision
            # Model: qwen2-vl:2b (imported by ollama-models)
            response = ollama.chat(model='qwen2-vl:2b', messages=[
                {
                    'role': 'user',
                    'content': 'Describe what you see in this image in 2-3 sentences.',
                    'images': [image_path]
                }
            ])
            
            description = response['message']['content']
            
            if description:
                self.log(f"Image description: {description}")
                self.speak_answer(description)
            else:
                self.log("No description generated", "WARN")
                self.set_state(State.IDLE)
                
        except Exception as e:
            self.log(f"Vision model failed: {e}", "ERROR")
            self.set_state(State.IDLE)
    
    def handle_command(self, command):
        """Handle incoming socket commands"""
        self.log(f"Received command: {command}")
        
        if command == "START_RECORDING":
            if self.state == State.IDLE:
                self.start_recording()
        
        elif command == "STOP_RECORDING":
            if self.state == State.LISTENING:
                self.stop_recording()
        
        elif command == "CAMERA_CAPTURE":
            if self.state == State.IDLE:
                self.capture_camera()
        
        elif command == "STATUS":
            return json.dumps({
                "state": self.state.value,
                "conversation_length": len(self.conversation_history)
            })
        
        elif command == "RESET":
            self.conversation_history = []
            self.log("Conversation history reset")
            self.set_state(State.IDLE)
        
        return "OK"
    
    def socket_handler(self, conn):
        """Handle socket client connection"""
        try:
            data = conn.recv(1024).decode('utf-8').strip()
            if data:
                response = self.handle_command(data)
                conn.sendall((response or "OK").encode('utf-8'))
        except Exception as e:
            self.log(f"Socket handler error: {e}", "ERROR")
        finally:
            conn.close()
    
    def start_socket_server(self):
        """Start Unix socket server for commands"""
        # Remove existing socket if present
        if os.path.exists(SOCKET_PATH):
            os.remove(SOCKET_PATH)
        
        self.socket_server = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        self.socket_server.bind(SOCKET_PATH)
        os.chmod(SOCKET_PATH, 0o666)  # Allow all users to connect
        self.socket_server.listen(5)
        
        self.log(f"Socket server listening on {SOCKET_PATH}")
        
        while True:
            try:
                conn, _ = self.socket_server.accept()
                # Handle in separate thread to not block
                thread = threading.Thread(target=self.socket_handler, args=(conn,))
                thread.daemon = True
                thread.start()
            except Exception as e:
                if self.socket_server:  # Only log if not shutting down
                    self.log(f"Socket server error: {e}", "ERROR")
                break
    
    def cleanup(self):
        """Cleanup resources"""
        self.log("Shutting down...")
        
        if self.recording_process:
            self.recording_process.terminate()
        
        if self.socket_server:
            self.socket_server.close()
        
        if os.path.exists(SOCKET_PATH):
            os.remove(SOCKET_PATH)
    
    def run(self):
        """Main run loop"""
        self.log("AI Chatbot service started")
        self.log(f"ASR: Whisper (model: {self.config['whisper']['model']})")
        self.log(f"LLM model: {self.config['llm']['model_path']}")
        
        # Setup signal handlers
        signal.signal(signal.SIGTERM, lambda s, f: self.cleanup() or sys.exit(0))
        signal.signal(signal.SIGINT, lambda s, f: self.cleanup() or sys.exit(0))
        
        try:
            self.start_socket_server()
        except KeyboardInterrupt:
            pass
        finally:
            self.cleanup()

if __name__ == "__main__":
    bot = AIChatBot()
    bot.run()
