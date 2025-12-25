# rpicam-apps - Raspberry Pi camera apps compatible with rpi-libcamera
# Works with rpi-libcamera 0.5.x (RPi5 pisp support)

SUMMARY = "Raspberry Pi camera capture and viewing applications"
DESCRIPTION = "rpicam-apps provides camera apps for Raspberry Pi including rpicam-still, rpicam-vid, etc."
HOMEPAGE = "https://github.com/raspberrypi/rpicam-apps"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=a0013d1b383d72ba4bdc5b750e7d1d77"

SRC_URI = "git://github.com/raspberrypi/rpicam-apps.git;protocol=https;branch=main"

# v1.5.3 - compatible with libcamera 0.5.x 
SRCREV = "50958df98d3cf77b54706a794226d556d649981c"
PV = "1.5.3"

S = "${WORKDIR}/git"

DEPENDS = " \
    rpi-libcamera \
    libpisp \
    libpng \
    libexif \
    jpeg \
    boost \
    libdrm \
    tiff \
"

inherit meson pkgconfig

EXTRA_OEMESON = " \
    -Denable_libav=disabled \
    -Denable_drm=enabled \
    -Denable_egl=disabled \
    -Denable_qt=disabled \
    -Denable_opencv=disabled \
    -Denable_tflite=disabled \
    -Denable_hailo=disabled \
    -Denable_imx500=false \
"

# Disable network access during build
do_compile[network] = "0"

# Runtime depends on libcamera
RDEPENDS:${PN} = "rpi-libcamera"

FILES:${PN} = " \
    ${bindir}/rpicam-* \
    ${bindir}/libcamera-* \
    ${libdir}/lib*.so.* \
    ${datadir}/* \
"

FILES:${PN}-dev = " \
    ${includedir}/* \
    ${libdir}/lib*.so \
    ${libdir}/pkgconfig/* \
"
