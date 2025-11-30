#!/bin/bash

################################################################################
#
# Entry point shell script to start the bitbake shell, after setting all the
# required project configurations and customizations
#
################################################################################

# Defining important variables
export SHATROX_RPI_SCRIPT_PATH=$(dirname $(readlink -e $0))
export SHATROX_RPI_BUILD_PATH="$SHATROX_RPI_SCRIPT_PATH/../build-rpi"
export EXTERNAL_LAYERS_PATH="$SHATROX_RPI_SCRIPT_PATH/../external"

# make sure build dir exists
mkdir -p $SHATROX_RPI_BUILD_PATH
cd $SHATROX_RPI_BUILD_PATH

# source the default poky init script
. $EXTERNAL_LAYERS_PATH/poky/oe-init-build-env $SHATROX_RPI_BUILD_PATH

# Custom Functions
function _print_info {
  echo -e "\e[34m\e[1m INFO: $1 \e[39m\e[0m"
}

function check_and_add_bblayer {
  if [ "$(grep $1 $SHATROX_RPI_BUILD_PATH/conf/bblayers.conf)" == "" ]; then
    sed -i '/BBLAYERS ?= \" \\/a\  '$EXTERNAL_LAYERS_PATH'\/'$2' \\' $SHATROX_RPI_BUILD_PATH/conf/bblayers.conf
    _print_info "Layer: ($1) was added successfully to bblayers.conf"
  fi
}

# include the extra project conf, and update bblayers paths
if [ "$(grep 'shatrox-rpi.conf' $SHATROX_RPI_BUILD_PATH/conf/local.conf)" == "" ]; then
  echo 'require conf/shatrox-rpi.conf' >> $SHATROX_RPI_BUILD_PATH/conf/local.conf
  _print_info "local.conf was updated successfully with the project extra shatrox-rpi.conf"
fi
check_and_add_bblayer 'meta-qt5' 'meta-qt5'
check_and_add_bblayer 'meta-raspberrypi' 'meta-raspberrypi'
check_and_add_bblayer 'meta-oe' 'meta-openembedded/meta-oe'
check_and_add_bblayer 'meta-multimedia' 'meta-openembedded/meta-multimedia'
check_and_add_bblayer 'meta-networking' 'meta-openembedded/meta-networking'
check_and_add_bblayer 'meta-perl' 'meta-openembedded/meta-perl'
check_and_add_bblayer 'meta-python' 'meta-openembedded/meta-python'
check_and_add_bblayer 'meta-webserver' 'meta-openembedded/meta-webserver'
check_and_add_bblayer 'meta-filesystems' 'meta-openembedded/meta-filesystems'
check_and_add_bblayer 'meta-xfce' 'meta-openembedded/meta-xfce'
check_and_add_bblayer 'meta-gnome' 'meta-openembedded/meta-gnome'
check_and_add_bblayer 'meta-initramfs' 'meta-openembedded/meta-initramfs'
check_and_add_bblayer 'meta-shatrox-rpi' '../meta-shatrox-rpi'

# Print summary and help for the user
echo ""
echo -e "\e[32m\e[1m═══════════════════════════════════════════════════════════════════════════\e[0m"
echo -e "\e[36m\e[1m                  🚀 SHATROX Raspberry Pi Yocto Build                     \e[0m"
echo -e "\e[32m\e[1m═══════════════════════════════════════════════════════════════════════════\e[0m"
echo ""
_print_info "✓ Environment configured successfully!"
_print_info "✓ All required layers have been added to bblayers.conf"
_print_info "✓ Custom configuration loaded from shatrox-rpi.conf"
echo ""
echo -e "\e[33m\e[1m📦 Available Build Targets:\e[0m"
echo ""
echo -e "  \e[1m1. Core Console Image\e[0m (~500MB - minimal, systemd, networking, TTS)"
echo -e "     \e[36m\$ bitbake -k sh-rpi-core-image\e[0m"
echo ""
echo -e "  \e[1m2. Qt5 EGLFS Image\e[0m (~800MB - GUI support, Qt5 libraries)"
echo -e "     \e[36m\$ bitbake -k sh-rpi-qt-image\e[0m"
echo ""
echo -e "  \e[1m3. AI Image\e[0m (~2GB - LLM inference, llama.cpp, Qwen2.5-1.5B)"
echo -e "     \e[36m\$ bitbake -k sh-rpi-ai-image\e[0m"
echo ""
echo -e "\e[32m\e[1m═══════════════════════════════════════════════════════════════════════════\e[0m"
echo ""

# Set custom prompt to indicate SHATROX bitbake environment
export PS1="\[\e[1;35m\][SHATROX-BB]\[\e[0m\] \[\e[1;34m\]\w\[\e[0m\] \$ "

# enter the bitbake shell
bash
