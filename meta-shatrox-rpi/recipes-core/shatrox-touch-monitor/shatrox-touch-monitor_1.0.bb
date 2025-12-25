SUMMARY = "SHATROX Touch Monitor Service"
DESCRIPTION = "Monitors touch screen interrupts with debouncing and noise filtering"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://shatrox-touch-monitor \
    file://shatrox-touch-monitor.service \
"

S = "${WORKDIR}"

inherit systemd

RDEPENDS:${PN} = " \
    bash \
    piper-tts \
"

do_install() {
    # Install script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/shatrox-touch-monitor ${D}${bindir}/
    
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/shatrox-touch-monitor.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = " \
    ${bindir}/shatrox-touch-monitor \
    ${systemd_system_unitdir}/shatrox-touch-monitor.service \
"

SYSTEMD_SERVICE:${PN} = "shatrox-touch-monitor.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
