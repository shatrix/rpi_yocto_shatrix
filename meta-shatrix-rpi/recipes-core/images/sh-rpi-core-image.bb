################################################################################
#
# Shatrix Basic Console Image, with dev tools
#
################################################################################
inherit core-image

DESCRIPTION = "Shatrix Basic Console Image, with dev tools"
HOMEPAGE    = "https://github.com/shatrix/rpi_yocto_shatrix"
SECTION     = "image"
PR          = "r001"

IMAGE_BASENAME             = "shatrix-rpi-core"
IMAGE_NAME                 = "${IMAGE_BASENAME}"
IMAGE_FSTYPES              = "tar.xz"
SDIMG_ROOTFS_TYPE          = "ext4.xz"
IMAGE_LINGUAS              = "en-us"
TARGETFILES_OUTPUTNAME    ?= "${IMAGE_BASENAME}-target"
TOOLCHAIN_OUTPUTNAME      ?= "${IMAGE_BASENAME}-sdk-${SDKMACHINE}"

BUILDHISTORY_COMMIT = "0"

IMAGE_FEATURES += "package-management"

DEPENDS += "bcm2835-bootfiles"

CORE_OS = " \
  kernel-modules \
  openssh openssh-keygen openssh-sftp-server \
  packagegroup-core-boot \
  tzdata \
"

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
  python3-modules \
  strace \
"

EXTRA_TOOLS_INSTALL = " \
  bzip2 \
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
"

RPI_STUFF = " \
  userland \
  bcm2835 \
  wiringpi \
"

IMAGE_INSTALL += " \
  ${CORE_OS} \
  ${DEV_SDK_INSTALL} \
  ${EXTRA_TOOLS_INSTALL} \
  ${RPI_STUFF} \
  ${WIFI_SUPPORT} \
"

set_local_timezone_UTC() {
  ln -sf /usr/share/zoneinfo/UTC ${IMAGE_ROOTFS}/etc/localtime
}

disable_bootlogd() {
  echo BOOTLOGD_ENABLE=no > ${IMAGE_ROOTFS}/etc/default/bootlogd
}

ROOTFS_POSTPROCESS_COMMAND += " \
  set_local_timezone_UTC ; \
  disable_bootlogd ; \
"

export IMAGE_BASENAME = "sh-rpi-core-image"
