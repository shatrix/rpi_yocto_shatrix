# SHATROX - Raspberry Pi Yocto Distribution

SHATROX is a custom Poky-based distribution for Raspberry Pi boards, optimized for IoT and embedded projects.

## Features

- **Multiple Image Variants**:
  - `sh-rpi-core-image`: Minimal console image with full hardware support
  - `sh-rpi-qt-image`: Qt5 EGLFS image for GUI applications
  - `sh-rpi-ai-image`: AI-enabled image with on-device LLM inference (llama.cpp + Qwen2.5-1.5B)

- **Text-to-Speech (TTS)**:
  - Built-in espeak text-to-speech engine
  - Auto-configured ALSA audio for USB Audio devices
  - Startup sound service that announces "System online" on boot
  - `speak` command-line utility for easy TTS from terminal

- **AI Capabilities** (AI image only):
  - Ollama for efficient on-device LLM inference
  - Llama 3.2:1b text model with Moondream vision support
  - **VOSK ASR** for offline speech recognition (~40MB model)
  - **OpenWakeWord** for "Hey Jarvis" wake word detection
  - **Piper TTS** for natural neural text-to-speech
  - Voice-interactive assistant with TTS integration
  - GPIO button interface for physical interaction (5 buttons: K1, K2, K3, K4, K8)
  - WebRTC VAD for automatic speech end detection
  - **Camera Vision** via rpi-libcamera v0.5.2 (PiSP pipeline for RPi5)
  - **Motor Control** with Waveshare Motor Driver HAT and obstacle avoidance
  - **QML Display** with CPU temp, volume indicator, and camera photo overlay

- **Hardware Support**:
  - Raspberry Pi 5 (primary target)
  - Wi-Fi, Bluetooth, GPIO, I2C, SPI, UART
  - USB Audio device support
  - HDMI display support
  - 3.5" SPI Touch Display (piscreen overlay)
  - **Camera Module 3** (imx708) via PiSP pipeline
  - **Waveshare Motor Driver HAT** (PCA9685 PWM + TB6612FNG)
  - **HC-SR04-P Ultrasonic Sensor** for obstacle detection

## Prerequisites
This project is based on Yocto Project 5.0 (scarthgap)
Please follow the instructions about "Compatible Linux Distribution" and "Build Host Packages" from this page
https://docs.yoctoproject.org/5.0.13/brief-yoctoprojectqs/index.html

## Build Host
Tested on Ubuntu 24.04.04 LTS 64-bit
The apparmor issue can be fixed using this workaround here:
https://discourse.ubuntu.com/t/ubuntu-24-04-lts-noble-numbat-release-notes/39890#p-99950-unprivileged-user-namespace-restrictions
Disable this restriction using a persistent setting by adding a new file (/etc/sysctl.d/60-apparmor-namespace.conf) with the following contents:
```bash
kernel.apparmor_restrict_unprivileged_userns=0
```

## Required Packages
```bash
sudo apt install build-essential chrpath cpio debianutils \
diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool \
locales python3 python3-git python3-jinja2 python3-pexpect \
python3-pip python3-subunit socat texinfo unzip wget xz-utils \
zstd bmap-tools

```

## Fetch everything
```bash
git clone --recursive https://github.com/shatrix/rpi_yocto_shatrix.git
```

## Build SHATROX Distro for RaspberryPi

```bash
cd rpi_yocto_shatrix
./scripts/start-bitbake-shell.sh
```

> [!NOTE]
> The script automatically downloads PyPI packages (onnxruntime, scipy, webrtcvad) that PyPI blocks from wget. These are required for the AI image's OpenWakeWord feature.

### Build Options

Choose the image that fits your needs:

**1. Core Console Image** (Minimal, ~500MB):
```bash
bitbake -k sh-rpi-core-image
```
Includes: systemd, networking, WiFi/BT, GPIO, TTS, development tools

**2. Qt5 EGLFS Image** (GUI support, ~800MB):
```bash
bitbake -k sh-rpi-qt-image
```
Includes: Everything from core + Qt5 libraries and EGLFS support

**3. AI Image** (LLM inference, ~2GB):
```bash
bitbake -k sh-rpi-ai-image
```
Includes: Everything from core + llama.cpp + Qwen2.5-1.5B model

## Known Issues

### AI Image - Current Limitations

> [!WARNING]
> The AI image is currently in **experimental state** with several known issues:

**1. Camera Integration (rpi-libcamera)**
- Camera support via **rpi-libcamera v0.5.2** (Raspberry Pi's libcamera fork)
- Uses **PiSP pipeline handler** for RPi5 Camera Module 3 (imx708)
- `libcamera-still` wrapper script provides compatibility with ai-chatbot.py
- **Status**: Builds successfully, untested on hardware

**2. Voice Input - VOSK ASR (Implemented)**
- Replaced Whisper with **VOSK ASR** for lower memory usage (~300MB vs 1.6GB)
- **OpenWakeWord** for "Hey Jarvis" wake word detection (uses ONNX Runtime)
- WebRTC VAD for automatic speech endpoint detection
- **Current status**: Implemented and working

**3. First Boot - Ollama Model Loading**
- Initial boot takes **4-5 minutes** for Ollama to download and import AI models
- The `model-import.service` waits for Ollama to be ready and pulls models via HTTP
- **Normal behavior**: During first boot, AI features won't respond until models are loaded
- **Check status**: `journalctl -u model-import.service -f`

**4. Disk Space Requirements**
- Default image creates a **6GB partition** regardless of SD card size
- **Minimum recommended**: 16GB SD card (boot partition uses ~5.5GB)
- **After first boot**: Expand root partition to utilize full SD card capacity:
  ```bash
  fdisk /dev/mmcblk0  # Delete and recreate partition 2 with full size
  reboot
  resize2fs /dev/mmcblk0p2
  ```

### Recommendations

For a **more stable experience**, consider:
- Using **Raspberry Pi OS** instead of Yocto (faster iteration, better hardware support)
- Reference: [PiSugar Whisplay AI Chatbot](https://github.com/PiSugar/whisplay-ai-chatbot) uses Raspberry Pi OS with VOSK for local ASR

## Flash the Image to MicroSD Card

1. Connect your microSD card to your PC
2. Check your card device name:
   ```bash
   lsblk
   ```
3. Flash the image (replace `/dev/sdX` with your actual device, e.g., `/dev/sdb`):

   **Core Image:**
   ```bash
   sudo bmaptool copy tmp/deploy/images/raspberrypi5/sh-rpi-core-image-raspberrypi5.rootfs.wic.bz2 /dev/sdX
   ```

   **Qt Image:**
   ```bash
   sudo bmaptool copy tmp/deploy/images/raspberrypi5/sh-rpi-qt-image-raspberrypi5.rootfs.wic.bz2 /dev/sdX
   ```

   **AI Image:**
   ```bash
   sudo bmaptool copy tmp/deploy/images/raspberrypi5/sh-rpi-ai-image-raspberrypi5.rootfs.wic.bz2 /dev/sdX
   ```

⚠️ **Warning:** Double-check the device name! This will erase all data on the target device.

## Using the Image

### Default Credentials
- Username: `root`
- Password: (none - direct login)

### TTS (Text-to-Speech) - Piper Neural Engine

The system includes **Piper neural TTS** for natural-sounding voice output. Two voices are pre-installed:

**Test the speak command:**
```bash
speak "Hello, this is Piper neural text to speech"
```

**Switch voices:**
```bash
# Switch to female voice (lessac - warm, Scarlett Johansson-like)
cd /usr/share/piper-voices
sudo ln -sf en_US-lessac-medium.onnx default.onnx
sudo ln -sf en_US-lessac-medium.onnx.json default.onnx.json

# Switch back to male voice (ryan - clear, default)
sudo ln -sf en_US-ryan-medium.onnx default.onnx
sudo ln -sf en_US-ryan-medium.onnx.json default.onnx.json
```

**Startup sound:**
The system speaks a welcome message on boot.

### GPIO Button Interface (AI image only)

The AI image includes a **button service** that monitors 8 GPIO buttons for physical interaction with Ruby (the AI assistant). All button presses are threaded for non-blocking operation.

**Button Mapping:**

| Button | GPIO | Function |
|--------|------|----------|
| **K1** | 5 | 🎉 Fun TTS message |
| **K2** | 6 | 👋 Greeting message |
| **K3** | 13 | 🤖 Ask: "What is Raspberry Pi?" |
| **K4** | 19 | 🛑 **Cancel** - Stop all activities |
| **K8** | 26 | 🔴 **Shutdown** system |

**Features:**
- **Cancel button (K4)**: Instantly stops any running AI query or speech
- **Auto-restart**: Pressing K3 while it's running will cancel and restart the query
- **Non-blocking**: All buttons remain responsive during AI processing
- **Threaded execution**: Press multiple buttons without waiting

**Service management:**
```bash
# Check button service status
systemctl status shatrox-buttons

# View button logs
journalctl -u shatrox-buttons -f

# Restart button service
systemctl restart shatrox-buttons
```

### AI Commands (AI image only)

The AI image includes **llama-server** which auto-starts on boot, keeping the model loaded in RAM for fast responses. Three models are included:
- **Qwen2.5-1.5B Q2_K** (default): Low memory, ~676MB, ~4-5s responses ⭐
- **Qwen2.5-1.5B Q4_K_M** (large): Best quality, ~986MB, ~6-7s responses
- **Qwen2.5-0.5B Q4_K_M** (small): Fastest, ~340MB, ~2-3s responses (lower quality)

**Voice-interactive AI assistant:**
```bash
llama-ask "What is Raspberry Pi?"
# AI responds with text and speaks the answer (~4-5 seconds)
# Limited to 100 tokens for faster responses

llama-ask --silent "Explain Linux kernel"
# AI responds with text only (no voice)
```

**Switch between models:**
```bash
# Use the fastest model (lower quality)
llama-model-switch small
systemctl restart llama-server

# Use the balanced model (default, lowest memory)
llama-model-switch medium
systemctl restart llama-server

# Use the best quality model (slower, more memory)
llama-model-switch large
systemctl restart llama-server

# List available models
llama-model-switch list
```

**Direct LLM inference (legacy):**
```bash
llama-quick-start "What is Raspberry Pi?"
# Slower: reloads model each time (10-15 seconds)
```

**Server management:**
```bash
# Check server status
systemctl status llama-server

# View server logs
journalctl -u llama-server -f

# Restart server
systemctl restart llama-server

# Manual start (if not using systemd)
llama-server-start
```

**HTTP API access:**
```bash
# Server runs on http://localhost:8080
curl http://localhost:8080/health

# Completion endpoint
curl http://localhost:8080/completion \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello", "n_predict": 100}'
```

## Dependencies

This project depends on the following Yocto layers:

| Layer | URI | Branch |
|-------|-----|--------|
| poky | git://git.yoctoproject.org/poky.git | scarthgap |
| meta-openembedded | git://git.openembedded.org/meta-openembedded | scarthgap |
| meta-qt5 | https://github.com/meta-qt5/meta-qt5 | scarthgap |
| meta-raspberrypi | git://git.yoctoproject.org/meta-raspberrypi | scarthgap |

All dependencies are included as git submodules and will be fetched automatically with `git clone --recursive`.


## Contributing
meta-shatrox-rpi layer maintainer: Sherif Mousa <sherif.e.mousa@gmail.com>

