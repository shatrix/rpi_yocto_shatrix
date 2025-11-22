SUMMARY = "Piper voice models for neural TTS"
DESCRIPTION = "Pre-trained ONNX voice models for Piper text-to-speech engine"
HOMEPAGE = "https://github.com/rhasspy/piper"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Download voice models from Hugging Face
SRC_URI = "https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_US/ryan/medium/en_US-ryan-medium.onnx;name=ryan \
           https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_US/ryan/medium/en_US-ryan-medium.onnx.json;name=ryan_json \
           https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_US/lessac/medium/en_US-lessac-medium.onnx;name=lessac \
           https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_US/lessac/medium/en_US-lessac-medium.onnx.json;name=lessac_json \
"

# SHA256 checksums
SRC_URI[ryan.sha256sum] = "abf4c274862564ed647ba0d2c47f8ee7c9b717d27bdad9219100eb310db4047a"
SRC_URI[ryan_json.sha256sum] = "44034c056cb15681b2ad494307c7f3f2e4499d1253c700c711fa0a4607ffe78d"
SRC_URI[lessac.sha256sum] = "5efe09e69902187827af646e1a6e9d269dee769f9877d17b16b1b46eeaaf019f"
SRC_URI[lessac_json.sha256sum] = "efe19c417bed055f2d69908248c6ba650fa135bc868b0e6abb3da181dab690a0"

S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Create voices directory
    install -d ${D}${datadir}/piper-voices
    
    # Install voice models
    install -m 0644 ${WORKDIR}/en_US-ryan-medium.onnx ${D}${datadir}/piper-voices/
    install -m 0644 ${WORKDIR}/en_US-ryan-medium.onnx.json ${D}${datadir}/piper-voices/
    install -m 0644 ${WORKDIR}/en_US-lessac-medium.onnx ${D}${datadir}/piper-voices/
    install -m 0644 ${WORKDIR}/en_US-lessac-medium.onnx.json ${D}${datadir}/piper-voices/
    
    # Create default symlink to ryan (male voice)
    ln -sf en_US-ryan-medium.onnx ${D}${datadir}/piper-voices/default.onnx
    ln -sf en_US-ryan-medium.onnx.json ${D}${datadir}/piper-voices/default.onnx.json
}

FILES:${PN} = "${datadir}/piper-voices/*"

# Mark as machine-specific due to large binary files
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Skip QA checks for these model files
INSANE_SKIP:${PN} = "already-stripped arch"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

# Allow network access during fetch
BB_FETCH_PREMIRRORONLY = "0"
BB_NO_NETWORK = "0"
