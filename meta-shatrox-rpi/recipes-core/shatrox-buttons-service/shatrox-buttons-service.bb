SUMMARY = "SHATROX Button Monitor Service"
DESCRIPTION = "GPIO button monitoring service that triggers LLM queries with TTS responses"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://shatrox-buttons.py \
    file://shatrox-buttons.service \
"

S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "shatrox-buttons.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} = " \
    python3-core \
    python3-gpiod \
"

do_install() {
    # Install the Python script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/shatrox-buttons.py ${D}${bindir}/shatrox-buttons

    # Install systemd service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/shatrox-buttons.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += " \
    ${bindir}/shatrox-buttons \
    ${systemd_system_unitdir}/shatrox-buttons.service \
"
