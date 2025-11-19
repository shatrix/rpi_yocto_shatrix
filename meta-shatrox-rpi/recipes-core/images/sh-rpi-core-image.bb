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

# Essential features
IMAGE_FEATURES += "package-management dev-pkgs debug-tweaks ssh-server-openssh"

# [VERIFIED] Hardware & Networking Packages
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
    udev-rules-rpi \
"

################################################################################
# Networking
################################################################################

NETWORK_PKGS = " \
    networkmanager \
    iproute2 \
    iptables \
    curl \
    wget \
"

################################################################################
# WiFi + Bluetooth stack
################################################################################

WIFI_BT_PKGS = " \
    linux-firmware-rpidistro-bcm43455 \
    bluez-firmware-rpidistro-bcm4345c5-hcd \
    iw \
    wpa-supplicant \
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
    python3-gpiod \
    raspi-gpio \
    rpi-gpio \
    python3-spidev \
"

################################################################################
# Minimal Dev Tools
################################################################################

DEV_MINIMAL = " \
    packagegroup-core-buildessential \
    git \
    pkgconfig \
    python3 \
    python3-pip \
    python3-modules \
    gdb \
    gdbserver \
"

################################################################################
# Minimal Utilities
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
    vim \
"

################################################################################
# POST PROCESS
################################################################################

set_local_timezone_UTC() {
    ln -sf /usr/share/zoneinfo/UTC ${IMAGE_ROOTFS}/etc/localtime
}

ROOTFS_POSTPROCESS_COMMAND += "set_local_timezone_UTC; "

export IMAGE_BASENAME = "sh-rpi-core-image"
