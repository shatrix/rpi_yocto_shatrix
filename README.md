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
  - llama.cpp for efficient CPU-based LLM inference
  - Pre-quantized Qwen2.5-1.5B model (optimized for 4GB RAM)
  - Auto-start HTTP API server (llama-server) for instant responses
  - Voice-interactive assistant (`llama-ask`) with TTS integration
  - Helper scripts: `llama-server-start`, `llama-quick-start`

- **Hardware Support**:
  - Raspberry Pi 5 (primary target)
  - Wi-Fi, Bluetooth, GPIO, I2C, SPI, UART
  - USB Audio device support
  - HDMI display support

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

### TTS Commands

Use the `speak` command from anywhere:
```bash
speak "Hello world"
speak "System is ready"
```

### AI Commands (AI image only)

The AI image includes **llama-server** which auto-starts on boot, keeping the model loaded in RAM for fast responses.

**Voice-interactive AI assistant:**
```bash
llama-ask "What is Raspberry Pi?"
# AI responds with text and speaks the answer (2-5 seconds)

llama-ask --silent "Explain Linux kernel"
# AI responds with text only (no voice)
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

