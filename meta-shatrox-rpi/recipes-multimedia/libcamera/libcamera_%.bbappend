# Add libpisp dependency for future RPi 5 camera support
# NOTE: libcamera 0.4.0 in Yocto scarthgap only supports rpi/vc4 pipeline
# The rpi/pisp pipeline for RPi5 requires libcamera 0.5.x or later
# Camera support on RPi5 requires upgrading libcamera recipe

DEPENDS += "libpisp"


