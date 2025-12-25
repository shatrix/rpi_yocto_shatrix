SUMMARY = "Scientific computing library for Python"
DESCRIPTION = "SciPy is open-source software for mathematics, science, and engineering"
HOMEPAGE = "https://scipy.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

# Pre-built wheel for aarch64/ARM64 Python 3.11
SRC_URI = "https://files.pythonhosted.org/packages/7f/39/7bc42c13d3b8d3e47f1f0bc2e08bf6bc5082f1e80c0ac755a7def53d4d33/scipy-1.11.4-cp311-cp311-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;downloadfilename=scipy-1.11.4-cp311-cp311-manylinux_2_17_aarch64.whl"
SRC_URI[sha256sum] = "00150c5eae7b610c32589dda259eacc7c4f1665aedf25d921907f4d08a951b1c"

S = "${WORKDIR}"

inherit python3-dir

RDEPENDS:${PN} = " \
    python3-core \
    python3-numpy \
    zlib \
"

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    ${PYTHON} -m zipfile -e ${WORKDIR}/scipy-1.11.4-cp311-cp311-manylinux_2_17_aarch64.whl ${D}${PYTHON_SITEPACKAGES_DIR}/
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"

# Avoid QA warnings about prebuilt binaries and bundled libraries (gfortran is bundled in wheel)
INSANE_SKIP:${PN} += "already-stripped ldflags file-rdeps"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
