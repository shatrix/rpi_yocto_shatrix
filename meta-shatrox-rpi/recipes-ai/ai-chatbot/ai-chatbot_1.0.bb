SUMMARY = "AI Chatbot Orchestration Service"
DESCRIPTION = "Python service for voice chat and camera vision with state machine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ai-chatbot.py \
           file://config.ini \
           file://ai-chatbot.service \
"

S = "${WORKDIR}"

inherit systemd

# Runtime dependencies
RDEPENDS:${PN} = " \
    python3-core \
    python3-json \
    python3-ollama \
    whisper-cpp \
    piper-tts \
    alsa-utils \
    bash \
    libcamera \
    libcamera-apps \
"

do_install() {
    # Install main service script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/ai-chatbot.py ${D}${bindir}/
    
    # Install configuration
    install -d ${D}${sysconfdir}/ai-chatbot
    install -m 0644 ${WORKDIR}/config.ini ${D}${sysconfdir}/ai-chatbot/
    
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ai-chatbot.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = " \
    ${bindir}/ai-chatbot.py \
    ${sysconfdir}/ai-chatbot/config.ini \
    ${systemd_system_unitdir}/ai-chatbot.service \
"

# Systemd service configuration
SYSTEMD_SERVICE:${PN} = "ai-chatbot.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
