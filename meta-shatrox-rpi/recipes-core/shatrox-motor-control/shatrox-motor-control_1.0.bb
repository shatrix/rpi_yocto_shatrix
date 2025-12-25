SUMMARY = "SHATROX Motor Control Service"
DESCRIPTION = "Motor control service for Waveshare Motor Driver HAT with obstacle avoidance"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://motor_controller.py \
    file://WavesharePCA9685.py \
    file://shatrox-motor-control.service \
"

S = "${WORKDIR}"

inherit systemd

# Runtime dependencies
RDEPENDS:${PN} = " \
    python3-core \
    python3-json \
    python3-threading \
    python3-smbus \
    python3-gpiod \
"

do_install() {
    # Install Python scripts
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/motor_controller.py ${D}${bindir}/
    
    # Install library module to Python site-packages
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    install -m 0644 ${WORKDIR}/WavesharePCA9685.py ${D}${PYTHON_SITEPACKAGES_DIR}/
    
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/shatrox-motor-control.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = " \
    ${bindir}/motor_controller.py \
    ${PYTHON_SITEPACKAGES_DIR}/WavesharePCA9685.py \
    ${systemd_system_unitdir}/shatrox-motor-control.service \
"

# Systemd service configuration
SYSTEMD_SERVICE:${PN} = "shatrox-motor-control.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
