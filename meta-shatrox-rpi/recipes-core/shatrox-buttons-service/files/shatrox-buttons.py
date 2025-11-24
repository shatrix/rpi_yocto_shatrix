#!/usr/bin/env python3
"""
SHATROX Button Monitor Service
Monitors 8 GPIO buttons and triggers LLM queries with TTS responses
"""

import gpiod
from gpiod.line import Direction, Bias, Edge
import time
import subprocess
import sys

# Button to GPIO mapping (K8 on GPIO 23)
BUTTON_MAP = {
    26: "K1",
    19: "K2",
    13: "K3",
    6:  "K4",
    5:  "K5",
    22: "K6",
    27: "K7",
    23: "K8"
}

# Button to question mapping
BUTTON_QUESTIONS = {
    "K1": "What is the Raspberry Pi board?",
    "K2": "Explain the Linux Kernel.",
    "K3": "Why is the sky blue?",
    "K4": "Why is the sky blue?",
    "K5": "Why is the sky blue?",
    "K6": "Why is the sky blue?",
    "K7": "Why is the sky blue?",
    "K8": "Why is the sky blue?"
}

INPUT_PINS = list(BUTTON_MAP.keys())
CHIP_PATH = "/dev/gpiochip0"
DEBOUNCE_MS = 500  # 500ms debounce time (increased to prevent double-clicks)
DISPLAY_TTY = "/dev/tty1"  # 3.5" SPI display

last_press_time = {}

# Open display TTY for writing
try:
    display_fd = open(DISPLAY_TTY, 'w', buffering=1)
except Exception as e:
    print(f"Warning: Could not open {DISPLAY_TTY}: {e}", file=sys.stderr)
    display_fd = None


def display_print(msg, end='\n'):
    """Print to both stdout (journald) and the display"""
    # Print to stdout (for journald/serial)
    print(msg, end=end, flush=True)
    
    # Also write to display if available
    if display_fd:
        try:
            display_fd.write(msg + end)
            display_fd.flush()
        except Exception as e:
            print(f"Display write error: {e}", file=sys.stderr)




def handle_button_press(button_name):
    """Handle a button press event using llama-ask"""
    question = BUTTON_QUESTIONS.get(button_name, "Unknown question")
    
    display_print(f"\n>>> [{button_name}] Question: {question}")
    display_print("━" * 60)
    
    try:
        # Run llama-ask and capture its output
        result = subprocess.Popen(
            ["llama-ask", question],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            bufsize=1  # Line buffered
        )
        
        # Display output line by line in real-time
        for line in result.stdout:
            display_print(line.rstrip())
        
        # Wait for completion
        result.wait(timeout=60)
        
        if result.returncode != 0:
            display_print(f"Error: llama-ask returned code {result.returncode}")
            
    except subprocess.TimeoutExpired:
        display_print("Error: Request timeout")
        if result:
            result.kill()
    except FileNotFoundError:
        display_print("Error: llama-ask command not found")
    except Exception as e:
        display_print(f"Error: {e}")


def run():
    """Main service loop"""
    line_settings = gpiod.LineSettings(
        direction=Direction.INPUT,
        bias=Bias.PULL_UP,
        edge_detection=Edge.FALLING
    )

    try:
        with gpiod.request_lines(
            CHIP_PATH,
            consumer="shatrox-buttons",
            config={tuple(INPUT_PINS): line_settings}
        ) as request:
            
            display_print("╔═══════════════════════════════════════════════════════════╗")
            display_print("║       SHATROX Button Service Active                      ║")
            display_print(f"║       Monitoring: {', '.join(BUTTON_MAP.values())}                             ║")
            display_print("╚═══════════════════════════════════════════════════════════╝")
            display_print("")
            
            while True:
                # Wait for button press event
                if request.wait_edge_events(timeout=None):
                    events = request.read_edge_events()
                    
                    for event in events:
                        gpio_pin = event.line_offset
                        button_name = BUTTON_MAP.get(gpio_pin, "Unknown")
                        
                        # Debounce check
                        now = time.time() * 1000
                        if now - last_press_time.get(gpio_pin, 0) > DEBOUNCE_MS:
                            last_press_time[gpio_pin] = now
                            handle_button_press(button_name)

    except OSError as e:
        display_print(f"GPIO Error: {e}")
        sys.exit(1)
    except KeyboardInterrupt:
        display_print("\n\n╔═══════════════════════════════════════════════════════════╗")
        display_print("║       Shatrox Button Service Stopped                     ║")
        display_print("╚═══════════════════════════════════════════════════════════╝")
        if display_fd:
            display_fd.close()
        sys.exit(0)


if __name__ == "__main__":
    run()

