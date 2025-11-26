SUMMARY = "llama.cpp - LLM inference in C/C++"
DESCRIPTION = "Port of Facebook's LLaMA model in C/C++ for efficient CPU inference"
HOMEPAGE = "https://github.com/ggerganov/llama.cpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1539dadbedb60aa18519febfeab70632"

DEPENDS = "cmake-native"

SRC_URI = "git://github.com/ggerganov/llama.cpp.git;protocol=https;branch=master \
           file://llama-quick-start \
           file://llama-server-start \
           file://llama-ask \
           file://llama-model-switch \
           file://llama-server.service \
"

# Pinned to stable tag b7164 (latest as of Nov 2025) for reproducible builds
# Update this when you want to upgrade llama.cpp
SRCREV = "b7164"

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd

# Build configuration for Raspberry Pi 5 (ARM Cortex-A76)
# Optimize for CPU inference, no GPU support
EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DGGML_NATIVE=OFF \
    -DGGML_LTO=ON \
    -DGGML_STATIC=OFF \
    -DGGML_CUDA=OFF \
    -DGGML_METAL=OFF \
    -DGGML_VULKAN=OFF \
    -DGGML_OPENMP=OFF \
    -DGGML_BLAS=OFF \
    -DGGML_RPC=OFF \
    -DLLAMA_BUILD_TESTS=OFF \
    -DLLAMA_BUILD_EXAMPLES=ON \
    -DLLAMA_BUILD_SERVER=ON \
    -DLLAMA_CURL=OFF \
    -DCMAKE_BUILD_TYPE=Release \
"

# Enable NEON for ARM SIMD optimization
EXTRA_OECMAKE:append:aarch64 = " -DGGML_CPU_ARM_ARCH=armv8.2-a"

do_install:append() {
    # Install helper scripts
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/llama-quick-start ${D}${bindir}/
    install -m 0755 ${WORKDIR}/llama-server-start ${D}${bindir}/
    install -m 0755 ${WORKDIR}/llama-ask ${D}${bindir}/
    install -m 0755 ${WORKDIR}/llama-model-switch ${D}${bindir}/
    
    # Install systemd service for llama-server auto-start
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/llama-server.service ${D}${systemd_system_unitdir}/
    
    # Create directory for models (will be populated by llama-models recipe)
    install -d ${D}${datadir}/llama-models
}

PACKAGES =+ "${PN}-server ${PN}-tools"

FILES:${PN} = " \
    ${bindir}/llama-cli \
    ${bindir}/llama-quick-start \
    ${libdir}/libllama.so.* \
    ${libdir}/libggml.so.* \
    ${libdir}/libggml-cpu.so.* \
    ${libdir}/libggml-base.so.* \
    ${libdir}/libmtmd.so.* \
    ${datadir}/llama-models \
"

FILES:${PN}-server = " \
    ${bindir}/llama-server \
    ${bindir}/llama-server-start \
    ${systemd_system_unitdir}/llama-server.service \
"

FILES:${PN}-tools = " \
    ${bindir}/llama-* \
    ${bindir}/convert_hf_to_gguf.py \
"

FILES:${PN}-dev = " \
    ${includedir}/* \
    ${libdir}/*.so \
    ${libdir}/pkgconfig \
    ${libdir}/cmake \
"

RDEPENDS:${PN} = "libstdc++ curl"
RDEPENDS:${PN}-server = "${PN} libstdc++ bash curl"
RDEPENDS:${PN}-tools = "${PN} bash curl python3-core"

# Systemd service configuration
SYSTEMD_PACKAGES = "${PN}-server"
SYSTEMD_SERVICE:${PN}-server = "llama-server.service"
SYSTEMD_AUTO_ENABLE:${PN}-server = "enable"

# Ensure we have enough tmp space for compilation
INHIBIT_PACKAGE_STRIP = "0"
INSANE_SKIP:${PN} = "already-stripped"
