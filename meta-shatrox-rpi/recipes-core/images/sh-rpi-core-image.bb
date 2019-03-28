################################################################################
#
# Shatrox Basic Console Image, with dev tools
#
################################################################################
inherit core-image

DESCRIPTION = "Shatrox Basic Console Image, with dev tools"
HOMEPAGE    = "https://github.com/shatrix/rpi_yocto_shatrix"
SECTION     = "image"
PR          = "r001"

# Base ROOTFS size 1GB
#IMAGE_ROOTFS_SIZE          = "1048576"
# Extra space in ROOTFS 6GB
#IMAGE_ROOTFS_EXTRA_SPACE   = "6291456"
BUILDHISTORY_COMMIT = "0"

IMAGE_FEATURES += "package-management dev-pkgs"

DEPENDS += "bcm2835-bootfiles zip-native python3-pip-native"

CORE_OS = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL} \
           openssh openssh-keygen openssh-sftp-server openssh-ssh openssh-scp \
           connman connman-plugin-ethernet dhcp-client \
           tzdata kernel-modules"

WIFI_SUPPORT = " \
  crda \
  iw \
  linux-firmware-rpidistro-bcm43455 \
  wpa-supplicant \
"

DEV_SDK_INSTALL = " \
  binutils \
  binutils-symlinks \
  coreutils \
  cpp \
  cpp-symlinks \
  diffutils \
  elfutils elfutils-binutils \
  file \
  g++ \
  g++-symlinks \
  gcc \
  gcc-symlinks \
  gdb \
  gdbserver \
  gettext \
  git \
  ldd \
  libstdc++ \
  libstdc++-dev \
  libtool \
  ltrace \
  make \
  pkgconfig \
  python \
  python-modules \
  python-pip \
  python3 \
  python3-modules \
  python3-pip \
  strace \
  perl-misc \
  perl-modules \
  perl \
  readline \
"

UTILITIES_INSTALL = " \
  bzip2 \
  tar \
  devmem2 \
  dosfstools \
  ethtool \
  fbset \
  findutils \
  grep \
  i2c-tools \
  iperf3 \
  iproute2 \
  iptables \
  less \
  lsof \
  nano \
  texinfo \
  usbutils \
  zlib \
  xz \
  watchdog \
  netcat-openbsd \
  nmap \
  ntp ntp-tickadj \
  procps \
  rng-tools \
  sysfsutils \
  tcpdump \
  unzip \
  util-linux \
  wget \
  zip \
  curl \
  nfs-utils \
  nfs-utils-client \
  openssl \
  opkg \
  opkg-utils \
  nfs-utils \
  nfs-utils-client \
  libusb1 \
  libxml2 \
  rsync \
"

RPI_STUFF = " \
  userland \
  wiringpi \
"

IMAGE_INSTALL += " \
  ${CORE_OS} \
  ${DEV_SDK_INSTALL} \
  ${UTILITIES_INSTALL} \
  ${RPI_STUFF} \
  ${WIFI_SUPPORT} \
"

set_local_timezone_UTC() {
  ln -sf /usr/share/zoneinfo/UTC ${IMAGE_ROOTFS}/etc/localtime
}

ROOTFS_POSTPROCESS_COMMAND += " \
  set_local_timezone_UTC ; \
"

export IMAGE_BASENAME = "sh-rpi-core-image"
