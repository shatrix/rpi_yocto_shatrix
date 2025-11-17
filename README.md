# Description
SHATROX is a custom Poky distribution for RaspberryPi boards
Can be used as a base for any IoT/Embedded projects

## Prerequisites
This project is based on Yocto Project 5.0 (scarthgap)
Please follow the instructions about "Compatible Linux Distribution" and "Build Host Packages" from this page
https://docs.yoctoproject.org/5.0.13/brief-yoctoprojectqs/index.html

## Build Host
Tested on Ubuntu 24.04.04 LTS 64-bit
The apparmor issue can be fixed using this workaround here:
https://discourse.ubuntu.com/t/ubuntu-24-04-lts-noble-numbat-release-notes/39890#p-99950-unprivileged-user-namespace-restrictions
Disable this restriction using a persistent setting by adding a new file (/etc/sysctl.d/60-apparmor-namespace.conf) with the following contents:
```bash
kernel.apparmor_restrict_unprivileged_userns=0
```

## Required Packages
```bash
sudo apt install build-essential chrpath cpio debianutils \
diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool \
locales python3 python3-git python3-jinja2 python3-pexpect \
python3-pip python3-subunit socat texinfo unzip wget xz-utils \
zstd bmap-tools

```

## Fetch everything
```bash
git clone --recursive git@github.com:shatrix/rpi_yocto_shatrix.git
```

## Build SHATROX Distro for RaspberryPi
```bash
cd rpi_yocto_shatrix
./scripts/start-bitbake-shell.sh
```
Start building the SHATROX basic image with this cmd
```bash
bitbake -k sh-rpi-core-image
```
Or build the SHATROX Qt image with this cmd
```bash
bitbake -k sh-rpi-qt-image
```

## Flash the output image to your MicroSD Card
Connect your microSD card to your PC, and check your card device name using 'lsblk' command
```bash
sudo bmaptool copy tmp/deploy/images/raspberrypi5/sh-rpi-core-image-raspberrypi5.rootfs.wic.bz2 /dev/sdX
```

## Depends on
URI: git://git.yoctoproject.org/poky.git
branch: scarthgap

URI: git://git.openembedded.org/meta-openembedded
branch: scarthgap

URI: https://github.com/meta-qt5/meta-qt5
branch: scarthgap

URI: git://git.yoctoproject.org/meta-raspberrypi
branch: scarthgap


## Contributing
meta-shatrox-rpi layer maintainer: Sherif Mousa <sherif.e.mousa@gmail.com>

