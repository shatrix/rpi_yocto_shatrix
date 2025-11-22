SUMMARY = "Piper neural text-to-speech engine"
DESCRIPTION = "Fast, local neural text-to-speech system with natural sounding voices"
HOMEPAGE = "https://github.com/rhasspy/piper"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Use pre-built binary release for arm64
SRC_URI = "https://github.com/rhasspy/piper/releases/download/2023.11.14-2/piper_linux_aarch64.tar.gz;name=piper"

SRC_URI[piper.sha256sum] = "fea0fd2d87c54dbc7078d0f878289f404bd4d6eea6e7444a77835d1537ab88eb"

S = "${WORKDIR}/piper"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Install piper binary
    install -d ${D}${bindir}
    install -m 0755 ${S}/piper ${D}${bindir}/
    
    # Install bundled libraries (they're directly in piper/ directory, not lib/)
    install -d ${D}${libdir}/piper
    install -m 0755 ${S}/*.so* ${D}${libdir}/piper/ 2>/dev/null || true
    
    # Install espeak-ng data directory to the location Piper expects
    if [ -d ${S}/espeak-ng-data ]; then
        install -d ${D}${datadir}/espeak-ng-data
        cp -r ${S}/espeak-ng-data/* ${D}${datadir}/espeak-ng-data/
    fi
    
    # Create LD config for piper libraries
    install -d ${D}${sysconfdir}/ld.so.conf.d
    echo "${libdir}/piper" > ${D}${sysconfdir}/ld.so.conf.d/piper.conf
}

FILES:${PN} = " \
    ${bindir}/piper \
    ${libdir}/piper \
    ${datadir}/* \
    ${sysconfdir}/ld.so.conf.d/piper.conf \
"

RDEPENDS:${PN} = "piper-voices"

# Run ldconfig after installation to register libraries
pkg_postinst:${PN}() {
    #!/bin/sh
    if [ -z "$D" ]; then
        ldconfig
    fi
}

# Skip QA checks
INSANE_SKIP:${PN} = "already-stripped ldflags file-rdeps"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Allow network access
BB_FETCH_PREMIRRORONLY = "0"
BB_NO_NETWORK = "0"
