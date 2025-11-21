#!/bin/bash
################################################################################
# Startup Sound Script
# Uses espeak to announce system boot completion
################################################################################

# Wait a moment for audio system to be fully ready
sleep 2

# Set default ALSA audio device (adjust if needed)
export AUDIODEV=hw:0,0

# Startup message
MESSAGE="Hi, Boot sequence complete. System online"

# Log to journal
echo "Playing startup message: ${MESSAGE}"

# Speak the message through default audio output
# -s: speed (words per minute, default 175)
# -a: amplitude (volume, 0-200, default 100)
# -v: voice variant
espeak -s 150 -a 150 -v en-us+m1 "${MESSAGE}" 2>&1 | logger -t startup-sound

# Exit successfully
exit 0
