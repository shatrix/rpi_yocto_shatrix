SUMMARY = "VOSK Speech Recognition Models"
DESCRIPTION = "Pre-trained English speech recognition model for VOSK"
HOMEPAGE = "https://alphacephei.com/vosk/models"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Small English model (~40MB) - good accuracy/size balance
VOSK_MODEL = "vosk-model-small-en-us-0.15"
SRC_URI = "https://alphacephei.com/vosk/models/${VOSK_MODEL}.zip"
SRC_URI[sha256sum] = "30f26242c4eb449f948e42cb302dd7a686cb29a3423a8367f99ff41780942498"

S = "${WORKDIR}"

do_install() {
    # Install model to /usr/share/vosk-models/
    install -d ${D}${datadir}/vosk-models
    cp -r ${WORKDIR}/${VOSK_MODEL} ${D}${datadir}/vosk-models/
    
    # Create symlink for default model
    ln -sf ${VOSK_MODEL} ${D}${datadir}/vosk-models/default
}

FILES:${PN} = "${datadir}/vosk-models"

# Large model files
INSANE_SKIP:${PN} = "already-stripped"
