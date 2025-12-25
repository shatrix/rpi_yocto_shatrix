# rpi-libcamera - Raspberry Pi's fork of libcamera with pisp support for RPi5
# Based on PR #1517 from meta-raspberrypi  
# This provides camera support for Raspberry Pi 5 (PiSP pipeline handler)

SUMMARY = "Raspberry Pi libcamera fork with PiSP support"
DESCRIPTION = "libcamera from Raspberry Pi's fork, with pisp pipeline support for RPi5 Camera Module"
HOMEPAGE = "https://github.com/raspberrypi/libcamera"
LICENSE = "LGPL-2.1-or-later & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-2.1-or-later.txt;md5=2a4f4fd2128ea2f65047ee63fbca9f68"

# Use Raspberry Pi's libcamera fork (not upstream) for pisp support
SRC_URI = "git://github.com/raspberrypi/libcamera.git;protocol=https;branch=main \
           file://libcamera-still \
          "

# v0.5.2+rpt20250903 - stable release with pisp support
SRCREV = "bfd68f786964636b09f8122e6c09c230367390e7"
PV = "0.5.2+rpt20250903"

S = "${WORKDIR}/git"

# Install wrapper script after meson install
do_install:append() {
    install -m 0755 ${WORKDIR}/libcamera-still ${D}${bindir}/
}

# Dependencies
DEPENDS = " \
    python3-pyyaml-native \
    python3-jinja2-native \
    python3-ply-native \
    gnutls \
    libevent \
    libyaml \
    systemd \
    glib-2.0 \
    libpisp \
"

inherit meson pkgconfig python3native

# Build only the pisp pipeline for RPi5 (not vc4 for RPi4)
EXTRA_OEMESON = " \
    -Dpipelines=rpi/pisp \
    -Dipas=rpi/pisp \
    -Dv4l2=true \
    -Dcam=enabled \
    -Dtest=false \
    -Ddocumentation=disabled \
    -Dgstreamer=disabled \
    -Dpycamera=disabled \
    -Dlc-compliance=disabled \
"

# Package configuration - include all IPA files in main package (essential for camera)
FILES:${PN} = " \
    ${bindir}/* \
    ${libdir}/libcamera*.so.* \
    ${libdir}/libcamera/* \
    ${libexecdir}/libcamera/* \
    ${datadir}/libcamera/* \
"

FILES:${PN}-dev = " \
    ${includedir}/* \
    ${libdir}/libcamera*.so \
    ${libdir}/pkgconfig/* \
"

# Make rpi-libcamera the preferred provider for libcamera
PROVIDES = "libcamera"
RPROVIDES:${PN} = "libcamera"

# Avoid conflict with upstream libcamera
RCONFLICTS:${PN} = "libcamera"

# Bash needed for libcamera-still wrapper script
RDEPENDS:${PN} = "bash"
