SUMMARY = "Whisper.cpp - High-performance inference of OpenAI's Whisper ASR model"
DESCRIPTION = "Port of OpenAI's Whisper model in C/C++ for efficient ARM64 inference"
HOMEPAGE = "https://github.com/ggerganov/whisper.cpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1539dadbedb60aa18519febfeab70632"

# Pin to stable release v1.8.2 (includes memory leak fixes from v1.7.2+ and ARM optimizations)
PV = "1.8.2"

# Download tiny.en-q5_1 model (quantized) - uses ~300MB RAM, vastly more efficient than FP16
# Note: repo moved from ggerganov to ggml-org
SRC_URI = "git://github.com/ggml-org/whisper.cpp.git;protocol=https;tag=v1.8.2 \
           https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en-q5_1.bin;name=model;downloadfilename=ggml-tiny.en-q5_1.bin \
           file://whisper-transcribe \
"

SRC_URI[model.sha256sum] = "c77c5766f1cef09b6b7d47f21b546cbddd4157886b3b5d6d4f709e91e66c7c2b"

S = "${WORKDIR}/git"

inherit cmake

# Static build with release optimizations
# Disable OpenMP to reduce threading overhead and memory usage on embedded
EXTRA_OECMAKE = "-DWHISPER_BUILD_TESTS=OFF \
                 -DWHISPER_BUILD_EXAMPLES=ON \
                 -DBUILD_SHARED_LIBS=OFF \
                 -DGGML_OPENMP=OFF \
                 -DCMAKE_BUILD_TYPE=Release \
"

do_install() {
    # Install main binary (whisper-cli replaced deprecated 'main')
    install -d ${D}${bindir}
    # Try whisper-cli first (new name), fallback to main (old name)
    if [ -f "${B}/bin/whisper-cli" ]; then
        install -m 0755 ${B}/bin/whisper-cli ${D}${bindir}/whisper-cpp
    else
        install -m 0755 ${B}/bin/main ${D}${bindir}/whisper-cpp
    fi
    
    # Install model directory
    install -d ${D}/usr/share/whisper-models
    install -m 0644 ${WORKDIR}/ggml-tiny.en-q5_1.bin ${D}/usr/share/whisper-models/
    
    # Install wrapper script
    install -m 0755 ${WORKDIR}/whisper-transcribe ${D}${bindir}/
}

FILES:${PN} = "${bindir}/whisper-cpp \
               ${bindir}/whisper-transcribe \
               /usr/share/whisper-models \
"

RDEPENDS:${PN} = "bash"
