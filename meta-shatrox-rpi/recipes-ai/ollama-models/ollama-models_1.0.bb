SUMMARY = "Ollama Models for Offline Use"
DESCRIPTION = "Qwen models downloaded from HuggingFace during build"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Download GGUF models from HuggingFace during build
SRC_URI = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf;name=text \
           https://huggingface.co/asuglia/Qwen2-VL-2B-Instruct-Q4_K_M-GGUF/resolve/main/qwen2-vl-2b-instruct-q4_k_m.gguf;name=vision \
           file://import-models.sh \
           file://model-import.service \
"

SRC_URI[text.sha256sum] = "6a1a2eb6d15622bf3c96857206351ba97e1af16c30d7a74ee38970e434e9407e"
SRC_URI[vision.sha256sum] = "37d33e24fc8dbd686b638e6c3d83d0a4332eebca26504d1b355bf87af4cc63de"

S = "${WORKDIR}"

inherit systemd

# Skip checksum validation for large model files
INSANE_SKIP:${PN} = "already-stripped"

do_install() {
    # Create model directory
    install -d ${D}/usr/share/ollama-models
    
    # Install GGUF model files
    install -m 0644 ${WORKDIR}/qwen2.5-1.5b-instruct-q4_k_m.gguf ${D}/usr/share/ollama-models/
    install -m 0644 ${WORKDIR}/qwen2-vl-2b-instruct-q4_k_m.gguf ${D}/usr/share/ollama-models/
    
    # Install import script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/import-models.sh ${D}${bindir}/
    
    # Install systemd service for one-time model import
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/model-import.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} = "/usr/share/ollama-models \
               ${bindir}/import-models.sh \
               ${systemd_system_unitdir}/model-import.service \
"

SYSTEMD_SERVICE:${PN} = "model-import.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} = "bash ollama"
