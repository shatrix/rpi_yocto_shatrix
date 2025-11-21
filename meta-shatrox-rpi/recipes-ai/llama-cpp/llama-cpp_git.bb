SUMMARY = "llama.cpp - LLM inference in C/C++"
DESCRIPTION = "Port of Facebook's LLaMA model in C/C++ for efficient CPU inference"
HOMEPAGE = "https://github.com/ggerganov/llama.cpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=042991f017d3b6f70f509fe903f31e4e"

DEPENDS = "cmake-native"

SRC_URI = "git://github.com/ggerganov/llama.cpp.git;protocol=https;branch=master \
           file://llama-quick-start \
           file://llama-server-start \
"

# Use latest stable tag (b4360 is recent stable, adjust as needed)
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

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
    -DCMAKE_BUILD_TYPE=Release \
"

# Enable NEON for ARM SIMD optimization
EXTRA_OECMAKE:append:aarch64 = " -DGGML_CPU_ARM_ARCH=armv8.2-a"

do_install:append() {
    # Install helper scripts
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/llama-quick-start ${D}${bindir}/
    install -m 0755 ${WORKDIR}/llama-server-start ${D}${bindir}/
    
    # Create directory for models (will be populated by llama-models recipe)
    install -d ${D}${datadir}/llama-models
}

PACKAGES =+ "${PN}-server ${PN}-tools"

FILES:${PN} = " \
    ${bindir}/llama-cli \
    ${bindir}/llama-quick-start \
    ${libdir}/libllama.so.* \
    ${libdir}/libggml.so.* \
    ${datadir}/llama-models \
"

FILES:${PN}-server = " \
    ${bindir}/llama-server \
    ${bindir}/llama-server-start \
"

FILES:${PN}-tools = " \
    ${bindir}/llama-* \
"

FILES:${PN}-dev = " \
    ${includedir}/* \
    ${libdir}/*.so \
    ${libdir}/pkgconfig \
"

RDEPENDS:${PN} = "libstdc++"
RDEPENDS:${PN}-server = "${PN} libstdc++"

# Ensure we have enough tmp space for compilation
INHIBIT_PACKAGE_STRIP = "0"
INSANE_SKIP:${PN} = "already-stripped"
