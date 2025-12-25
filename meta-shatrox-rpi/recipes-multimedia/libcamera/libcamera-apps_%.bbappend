# Skip libcamera-apps - using rpi-libcamera instead
# libcamera-apps 1.4.2 is incompatible with rpi-libcamera 0.5.2
SKIP_RECIPE[libcamera-apps] = "Using rpi-libcamera instead"
