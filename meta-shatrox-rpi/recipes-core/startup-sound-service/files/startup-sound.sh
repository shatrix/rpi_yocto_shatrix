#!/bin/bash
################################################################################
# Startup Sound Script
# Uses Piper neural TTS to announce system boot completion
################################################################################

# Wait for audio system and ldconfig to complete
sleep 3

# Startup message
MESSAGE="Hi, Boot sequence complete. System online"

# Log to journal
echo "Playing startup message: ${MESSAGE}"

# Use the speak command (Piper TTS)
speak "${MESSAGE}"

# Exit successfully
exit 0
