SUMMARY = "SHATROX QML Display Application"
DESCRIPTION = "QML-based display app for showing AI interactions on 3.5 inch screen"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://shatrox-display.qml \
    file://shatrox-display-start \
    file://shatrox-display.service \
"

S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "shatrox-display.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} = " \
    qtdeclarative-qmlplugins \
    qtquickcontrols2 \
    qtdeclarative-tools \
    bash \
"

do_install() {
    # Install QML file
    install -d ${D}${datadir}/shatrox
    install -m 0644 ${WORKDIR}/shatrox-display.qml ${D}${datadir}/shatrox/
    
    # Install launcher script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/shatrox-display-start ${D}${bindir}/
    
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/shatrox-display.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += " \
    ${datadir}/shatrox/shatrox-display.qml \
    ${bindir}/shatrox-display-start \
    ${systemd_system_unitdir}/shatrox-display.service \
"
