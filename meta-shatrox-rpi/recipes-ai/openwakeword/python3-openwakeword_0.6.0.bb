SUMMARY = "OpenWakeWord - Open-source wake word detection"
DESCRIPTION = "A lightweight and efficient wake word detection library using ONNX models"
HOMEPAGE = "https://github.com/dscripka/openWakeWord"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://files.pythonhosted.org/packages/source/o/openwakeword/openwakeword-0.6.0.tar.gz"
SRC_URI[sha256sum] = "36858d90f1183e307485597a912a4e3c3384b14ea9923f83feaffae7c1565565"

S = "${WORKDIR}/openwakeword-0.6.0"

inherit setuptools3

RDEPENDS:${PN} = " \
    python3-core \
    python3-numpy \
    python3-onnxruntime \
    python3-scipy \
"
