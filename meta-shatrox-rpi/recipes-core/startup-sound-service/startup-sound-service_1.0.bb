SUMMARY = "Startup sound service using espeak text-to-speech"
DESCRIPTION = "Systemd service that plays a startup sound message using espeak TTS"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://startup-sound.service \
    file://startup-sound.sh \
"

S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "startup-sound.service"
SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN} = "espeak alsa-utils bash"

do_install() {
    # Install systemd service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/startup-sound.service ${D}${systemd_system_unitdir}/

    # Install startup script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/startup-sound.sh ${D}${bindir}/
}

FILES:${PN} += "${systemd_system_unitdir}/startup-sound.service"
