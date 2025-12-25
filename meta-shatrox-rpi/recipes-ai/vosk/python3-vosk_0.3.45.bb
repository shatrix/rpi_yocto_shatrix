SUMMARY = "VOSK Offline Speech Recognition"
DESCRIPTION = "Offline speech recognition API for Python using Vosk library"
HOMEPAGE = "https://alphacephei.com/vosk/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Use pre-built aarch64 wheel (includes native libvosk.so)
SRC_URI = "file://vosk-0.3.45-py3-none-manylinux2014_aarch64.whl"
SRC_URI[sha256sum] = "54efb47dd890e544e9e20f0316413acec7f8680d04ec095c6140ab4e70262704"

S = "${WORKDIR}"

inherit python3native

# Need unzip to extract wheel
DEPENDS += "unzip-native"

# Runtime dependencies
RDEPENDS:${PN} += " \
    python3-core \
    python3-json \
    python3-cffi \
"

# VOSK needs access to the model files
RDEPENDS:${PN} += "vosk-models"

do_install() {
    # Install wheel contents to Python site-packages
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    
    # Unzip wheel and install
    unzip -o ${WORKDIR}/vosk-0.3.45-py3-none-manylinux2014_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"

# Wheel contains prebuilt native libs - skip QA checks
INSANE_SKIP:${PN} = "already-stripped ldflags"
