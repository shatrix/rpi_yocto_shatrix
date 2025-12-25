SUMMARY = "SHATROX Volume Monitor Service"
DESCRIPTION = "Monitors speaker volume and updates status file for QML display"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://shatrox-volume-monitor \
    file://shatrox-volume-monitor.service \
"

S = "${WORKDIR}"

inherit systemd

RDEPENDS:${PN} = " \
    bash \
    alsa-utils \
"

do_install() {
    # Install script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/shatrox-volume-monitor ${D}${bindir}/
    
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/shatrox-volume-monitor.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = " \
    ${bindir}/shatrox-volume-monitor \
    ${systemd_system_unitdir}/shatrox-volume-monitor.service \
"

SYSTEMD_SERVICE:${PN} = "shatrox-volume-monitor.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
