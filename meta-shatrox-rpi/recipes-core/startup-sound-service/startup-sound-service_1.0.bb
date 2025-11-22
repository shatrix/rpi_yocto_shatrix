SUMMARY = "Startup sound service for Raspberry Pi"
DESCRIPTION = "Plays welcome message on boot using Piper TTS"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://startup-sound.service \
           file://startup-sound.sh \
           file://detect-audio.sh \
           file://speak \
"

S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "startup-sound.service"
SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN} = "piper-tts alsa-utils bash"

do_install() {
    # Install systemd service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/startup-sound.service ${D}${systemd_system_unitdir}/

    # Install startup script and helper
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/startup-sound.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/detect-audio.sh ${D}${bindir}/
    install -m 0755 ${WORKDIR}/speak ${D}${bindir}/
}

FILES:${PN} += "${systemd_system_unitdir}/startup-sound.service"
