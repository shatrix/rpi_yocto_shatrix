################################################################################
#
# Shatrox Minimal Console Image for Raspberry Pi 5 (scarthgap)
#
# With full RPi 5 hardware support (Wi-Fi, BT, GPIO, Network).
#
################################################################################
inherit core-image

DESCRIPTION = "Minimal console-only image for Raspberry Pi 5 with full hardware support (systemd)."
HOMEPAGE    = "https://github.com/shatrix/rpi_yocto_shatrix"
LICENSE     = "MIT"
PR          = "r002"

# Set a minimal default rootfs size 1GB
# The 1GB extra space is for package management headroom
IMAGE_ROOTFS_SIZE = "1048576"
IMAGE_ROOTFS_EXTRA_SPACE = "1048576"

# Enable package management + dev debugging
IMAGE_FEATURES += "package-management dev-pkgs debug-tweaks"

IMAGE_INSTALL += " \
    ${CORE_SHATROX} \
    ${NETWORK_PKGS} \
    ${WIFI_BT_PKGS} \
    ${GPIO_PKGS} \
    ${DEV_MINIMAL} \
    ${UTILITIES_MIN} \
"

################################################################################
# CORE SYSTEM (systemd)
################################################################################

CORE_SHATROX = " \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    packagegroup-core-boot \
    packagegroup-base \
    systemd \
    base-files \
    base-passwd \
    busybox \
    bash \
    tzdata \
    netbase \
    kernel-modules \
    ethtool \
    fuse \
    dbus \
    udev \
"

################################################################################
# Networking (systemd-native)
################################################################################

NETWORK_PKGS = " \
    iproute2 \
    iptables \
    curl \
    wget \
"

################################################################################
# WiFi + Bluetooth stack (RPi5 BCM43455)
################################################################################

WIFI_BT_PKGS = " \
    linux-firmware-rpidistro-bcm43455 \
    bluez-firmware-rpidistro-bcm4345c5-hcd \
    iw \
    wpa-supplicant \
    wpa-supplicant-cli \
    hostapd \
    bluez5 \
    bluez5-obex \
    bluez5-noinst-tools \
    wireless-regdb-static \
"

################################################################################
# GPIO, I2C, SPI, UART Support
################################################################################

GPIO_PKGS = " \
    i2c-tools \
    libgpiod \
    libgpiod-tools \
"

################################################################################
# Minimal Dev Tools (on-target SDK)
################################################################################

DEV_MINIMAL = " \
    gcc \
    g++ \
    make \
    gdb \
    gdbserver \
    pkgconfig \
    python3 \
    python3-pip \
"

################################################################################
# Minimal Utilities for troubleshooting
################################################################################

UTILITIES_MIN = " \
    bzip2 \
    tar \
    unzip \
    xz \
    nano \
    htop \
    iputils \
    traceroute \
    usbutils \
    procps \
    rsync \
"

################################################################################
# POST PROCESS
################################################################################

# Enable systemd networking by default
SYSTEMD_DEFAULT_TARGET = "multi-user.target"
SYSTEMD_PACKAGES = "packagegroup-core-boot"
SYSTEMD_SERVICE:${PN}:append = " systemd-networkd.service systemd-resolved.service"

set_local_timezone_UTC() {
    ln -sf /usr/share/zoneinfo/UTC ${IMAGE_ROOTFS}/etc/localtime
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone_UTC; \
    write_image_manifest; \
"

################################################################################
export IMAGE_BASENAME = "sh-rpi-core-image"
