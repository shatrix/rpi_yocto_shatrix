# Description
    SHATROX is a custom Poky distribution for RaspberryPi boards
    Will be used as a base for IoT projects

## Prerequisites
    This project is based on Yocto Project 2.6 (thud)
    Please follow the instructions about "Compatible Linux Distribution" and "Build Host Packages" from this page
    https://www.yoctoproject.org/docs/2.6.1/brief-yoctoprojectqs/brief-yoctoprojectqs.html

## Build Host
    Tested on Ubuntu 18.04 LTS 64-bit

## Required Packages
```bash
$ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib \
build-essential chrpath socat cpio python python3 python3-pip python3-pexpect \
xz-utils debianutils iputils-ping libsdl1.2-dev xterm
```

## Fetch everything
```bash
$ git clone --recursive git@github.com:shatrix/rpi_yocto_shatrix.git
```

## Build SHATROX Distro for RaspberryPi
```bash
$ cd rpi_yocto_shatrix
$ ./scripts/start-bitbake-shell.sh
$ bitbake -k sh-rpi-core-image
```

## Flash the output image to your MicroSD Card
Connect your microSD card to your PC, and check your card device name using 'lsblk' command
```bash
$ sudo dd bs=4M if=tmp/deploy/images/raspberrypi3/sh-rpi-core-image.rootfs.rpi-sdimg of=/dev/sdX conv=fsync status=progress
```

## Depends on
    URI: git://git.yoctoproject.org/poky.git
    branch: thud

    URI: git://git.openembedded.org/meta-openembedded
    branch: thud

    URI: https://github.com/meta-qt5/meta-qt5
    branch: thud

    URI: git://git.yoctoproject.org/meta-raspberrypi
    branch: thud


## Contributing
    meta-shatrox-rpi layer maintainer: Sherif Mousa <sherif.e.mousa@gmail.com>

