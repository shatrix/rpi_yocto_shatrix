################################################################################
#
# Shatrox AI Image for Raspberry Pi 5 (scarthgap)
#
# Extends sh-rpi-core-image with AI capabilities (LLM inference)
#
################################################################################

# Inherit from the core image
require sh-rpi-core-image.bb

DESCRIPTION = "AI-enabled image for Raspberry Pi 5 with LLM inference capabilities (llama.cpp + Qwen2.5-1.5B)"
PR = "r001"

# Add AI-specific packages
IMAGE_INSTALL += " \
    ${AI_PKGS} \
"

################################################################################
# AI Packages
################################################################################

AI_PKGS = " \
    llama-cpp \
    llama-models \
    python3-numpy \
    cmake \
    tmux \
"

export IMAGE_BASENAME = "sh-rpi-ai-image"
