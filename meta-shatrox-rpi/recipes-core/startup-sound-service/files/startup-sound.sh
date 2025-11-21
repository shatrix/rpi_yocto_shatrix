#!/bin/bash
################################################################################
# Startup Sound Script
# Uses espeak to announce system boot completion
################################################################################

# Wait a moment for audio system to be fully ready
sleep 2

# Detect USB Audio device dynamically
AUDIO_DEVICE=$(/usr/bin/detect-audio.sh)
echo "Using audio device: ${AUDIO_DEVICE}"

# Startup message
MESSAGE="Hi, Boot sequence complete. System online"

# Log to journal
echo "Playing startup message: ${MESSAGE}"

# Speak the message through detected audio device
# espeak outputs to stdout, aplay plays it with format conversion
# -s: speed (words per minute, default 175)
# -v: voice variant
espeak --stdout -s 150 -v en-us+m1 "${MESSAGE}" 2>&1 | \
    aplay -D"${AUDIO_DEVICE}" 2>&1 | logger -t startup-sound

# Exit successfully
exit 0
