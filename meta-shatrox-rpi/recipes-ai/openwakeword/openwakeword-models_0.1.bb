SUMMARY = "OpenWakeWord pre-trained models"
DESCRIPTION = "Pre-trained ONNX models for wake word detection (Hey Jarvis, Alexa)"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Download models from OpenWakeWord GitHub releases
SRC_URI = " \
    https://github.com/dscripka/openWakeWord/releases/download/v0.5.1/hey_jarvis_v0.1.onnx;name=hey_jarvis \
    https://github.com/dscripka/openWakeWord/releases/download/v0.5.1/alexa_v0.1.onnx;name=alexa \
"
SRC_URI[hey_jarvis.sha256sum] = "94a13cfe60075b132f6a472e7e462e8123ee70861bc3fb58434a73712ee0d2cb"
SRC_URI[alexa.sha256sum] = "6ff566a01d12670e8d9e3c59da32651db1575d17272a601b7f8a39283dfbae3e"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${datadir}/openwakeword-models
    install -m 0644 ${WORKDIR}/hey_jarvis_v0.1.onnx ${D}${datadir}/openwakeword-models/
    install -m 0644 ${WORKDIR}/alexa_v0.1.onnx ${D}${datadir}/openwakeword-models/
}

FILES:${PN} = "${datadir}/openwakeword-models"
