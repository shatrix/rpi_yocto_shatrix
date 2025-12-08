# Add libpisp dependency for RPi 5 camera support
# In libcamera 0.4.0, pisp is detected automatically within rpi/vc4 pipeline when libpisp is present
DEPENDS += "libpisp"
