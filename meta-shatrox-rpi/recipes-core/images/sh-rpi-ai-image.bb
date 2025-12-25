################################################################################
#
# Shatrox AI Image for Raspberry Pi 5 (scarthgap)
#
# Extends sh-rpi-core-image with AI capabilities (LLM inference)
#
################################################################################

# Inherit from the core image
require sh-rpi-core-image.bb

DESCRIPTION = "AI-enabled image for Raspberry Pi 5 with LLM inference capabilities (Ollama + Qwen models)"
PR = "r002"

# Add AI-specific packages
IMAGE_INSTALL += " \
    ${AI_PKGS} \
    ${STRESS_TEST_PKGS} \
"

################################################################################
# AI Packages
################################################################################

AI_PKGS = " \
    ollama \
    ollama-models \
    python3-ollama \
    python3-numpy \
    ai-chatbot \
    cmake \
    tmux \
    shatrox-buttons-service \
    shatrox-display-app \
    startup-sound-service \
    shatrox-volume-monitor \
    shatrox-motor-control \
    ${QT5_MINIMAL} \
"

################################################################################
# Minimal Qt5 for 3.5" Display GUI (EGLFS)
################################################################################

QT5_MINIMAL = " \
    qtbase \
    qtbase-plugins \
    qtdeclarative \
    qtdeclarative-qmlplugins \
    qtquickcontrols2 \
    qt5-env \
"

################################################################################
# Stress Testing & System Monitoring
################################################################################

STRESS_TEST_PKGS = " \
    stress-ng \
    sysbench \
    sysstat \
"

export IMAGE_BASENAME = "sh-rpi-ai-image"
