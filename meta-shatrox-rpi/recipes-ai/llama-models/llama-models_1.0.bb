SUMMARY = "Pre-quantized LLM models for llama.cpp"
DESCRIPTION = "GGUF quantized models optimized for Raspberry Pi 5 (4GB RAM)"
HOMEPAGE = "https://huggingface.co"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Qwen2.5 models - using bartowski's quantizations (well-maintained, optimized)
SRC_URI = "https://huggingface.co/bartowski/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf;name=qwen15b_q4km \
           https://huggingface.co/bartowski/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/Qwen2.5-1.5B-Instruct-Q2_K.gguf;name=qwen15b_q2k \
           https://huggingface.co/bartowski/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/Qwen2.5-0.5B-Instruct-Q4_K_M.gguf;name=qwen05b \
           file://README.md \
"

# SHA256 checksums for model files
SRC_URI[qwen15b_q4km.sha256sum] = "1adf0b11065d8ad2e8123ea110d1ec956dab4ab038eab665614adba04b6c3370"
SRC_URI[qwen15b_q2k.sha256sum] = "a8880f0de2348db67d00519ef7f4b40326ef67012bf5f2e90bd1d47474e2355c"
SRC_URI[qwen05b.sha256sum] = "6eb923e7d26e9cea28811e1a8e852009b21242fb157b26149d3b188f3a8c8653"

# This is a binary model file, no compilation needed
S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Create model directory
    install -d ${D}${datadir}/llama-models
    
    # Install all three model files
    install -m 0644 ${WORKDIR}/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf \
        ${D}${datadir}/llama-models/
    install -m 0644 ${WORKDIR}/Qwen2.5-1.5B-Instruct-Q2_K.gguf \
        ${D}${datadir}/llama-models/
    install -m 0644 ${WORKDIR}/Qwen2.5-0.5B-Instruct-Q4_K_M.gguf \
        ${D}${datadir}/llama-models/
    
    # Install README
    install -m 0644 ${WORKDIR}/README.md ${D}${datadir}/llama-models/
    
    # Create symlinks for easy access
    # Default: Q2_K (medium) - smallest 1.5B variant, good for low memory
    ln -sf Qwen2.5-1.5B-Instruct-Q2_K.gguf \
        ${D}${datadir}/llama-models/default
    ln -sf Qwen2.5-1.5B-Instruct-Q2_K.gguf \
        ${D}${datadir}/llama-models/medium
    # Large: Q4_K_M - best quality
    ln -sf Qwen2.5-1.5B-Instruct-Q4_K_M.gguf \
        ${D}${datadir}/llama-models/large
    # Small: 0.5B - fastest
    ln -sf Qwen2.5-0.5B-Instruct-Q4_K_M.gguf \
        ${D}${datadir}/llama-models/small
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
