SUMMARY = "ONNX Runtime - Cross-platform Machine Learning inferencing"
DESCRIPTION = "ONNX Runtime is a performance-focused inference engine for ONNX models"
HOMEPAGE = "https://onnxruntime.ai/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Pre-built wheel for aarch64/ARM64 - using file from Yocto downloads
# Downloaded via: pip3 download onnxruntime==1.16.3 --no-deps --only-binary=:all: --platform manylinux_2_17_aarch64 --python-version 311
SRC_URI = "https://files.pythonhosted.org/packages/f3/d6/0417aaf99f8a2ff8dbb0a3c22d5dac4e25a8e76cdd668c9eb3c9e36ac7a4/onnxruntime-1.16.3-cp311-cp311-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;downloadfilename=onnxruntime-1.16.3-cp311-cp311-manylinux_2_17_aarch64.whl"
SRC_URI[sha256sum] = "00cccc37a5195c8fca5011b9690b349db435986bd508eb44c9fce432da9228a4"

S = "${WORKDIR}"

inherit python3-dir

# Runtime dependencies (only essential for inference)
RDEPENDS:${PN} = " \
    python3-core \
    python3-numpy \
    python3-protobuf \
    python3-packaging \
"

do_install() {
    # Extract wheel and install
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    
    ${PYTHON} -m zipfile -e ${WORKDIR}/onnxruntime-1.16.3-cp311-cp311-manylinux_2_17_aarch64.whl ${D}${PYTHON_SITEPACKAGES_DIR}/
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"

# Avoid QA warnings about prebuilt binaries
INSANE_SKIP:${PN} += "already-stripped ldflags"

# This is a prebuilt binary package
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
