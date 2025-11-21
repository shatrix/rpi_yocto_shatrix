SUMMARY = "Pre-quantized LLM models for llama.cpp"
DESCRIPTION = "GGUF quantized models optimized for Raspberry Pi 5 (4GB RAM)"
HOMEPAGE = "https://huggingface.co"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Qwen2.5-1.5B-Instruct Q4_K_M quantized model
# Using bartowski's quantizations (well-maintained, optimized)
SRC_URI = "https://huggingface.co/bartowski/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf;name=qwen \
           file://README.md \
"

# SHA256 checksum for the model file
SRC_URI[qwen.sha256sum] = "1adf0b11065d8ad2e8123ea110d1ec956dab4ab038eab665614adba04b6c3370"

# This is a binary model file, no compilation needed
S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Create model directory
    install -d ${D}${datadir}/llama-models
    
    # Install the model file
    install -m 0644 ${WORKDIR}/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf \
        ${D}${datadir}/llama-models/
    
    # Install README
    install -m 0644 ${WORKDIR}/README.md ${D}${datadir}/llama-models/
    
    # Create symlink to default model
    ln -sf Qwen2.5-1.5B-Instruct-Q4_K_M.gguf \
        ${D}${datadir}/llama-models/default
}

FILES:${PN} = " \
    ${datadir}/llama-models/* \
"

RDEPENDS:${PN} = "llama-cpp"

# Mark as machine-specific due to large binary
PACKAGE_ARCH = "${MACHINE_ARCH}"

# This is a large file, skip some QA checks
INSANE_SKIP:${PN} = "already-stripped arch"

# Prevent stripping of the model file
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

# Allow network access during do_fetch
BB_FETCH_PREMIRRORONLY = "0"
BB_NO_NETWORK = "0"
