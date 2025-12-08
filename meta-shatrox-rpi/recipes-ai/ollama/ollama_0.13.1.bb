SUMMARY = "Ollama - Run LLMs locally"
DESCRIPTION = "Local LLM server with REST API"
HOMEPAGE = "https://ollama.com"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PV = "0.13.1"

# Download from GitHub (BitBake will use downloads/ cache)
SRC_URI = "https://github.com/ollama/ollama/releases/download/v${PV}/ollama-linux-arm64.tgz;name=binary;downloadfilename=ollama-linux-arm64.tgz \
           file://ollama.service \
"

# Checksum for downloaded binary
SRC_URI[binary.sha256sum] = "b1747f3f9aefead61a918b49372028faa68dd0b9f141b7f25b05afb327a3551d"

S = "${WORKDIR}"

inherit systemd

# Skip various QA checks for pre-compiled binary
INSANE_SKIP:${PN} = "already-stripped ldflags file-rdeps"

do_install() {
    # Debug: show what files are in WORKDIR
    echo "=== Files in WORKDIR ==="
    ls -la ${WORKDIR}/
    
    # Extract .tgz to temporary location
    install -d ${WORKDIR}/extract
    
    # Try both possible locations
    if [ -f "${WORKDIR}/ollama-linux-arm64.tgz" ]; then
        tar -xzf ${WORKDIR}/ollama-linux-arm64.tgz -C ${WORKDIR}/extract/
    elif [ -f "${DL_DIR}/ollama-linux-arm64.tgz" ]; then
        tar -xzf ${DL_DIR}/ollama-linux-arm64.tgz -C ${WORKDIR}/extract/
    else
        echo "ERROR: Cannot find ollama-linux-arm64.tgz"
        exit 1
    fi
    
    # Install binary
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/extract/bin/ollama ${D}${bindir}/
    
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ollama.service ${D}${systemd_system_unitdir}/
    
    # Create model directory
    install -d ${D}/var/lib/ollama/models
}

SYSTEMD_SERVICE:${PN} = "ollama.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} = "${bindir}/ollama \
               ${systemd_system_unitdir}/ollama.service \
               /var/lib/ollama \
"

RDEPENDS:${PN} = "bash"
