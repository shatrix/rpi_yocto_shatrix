#!/bin/bash

################################################################################
#
# Entry point shell script to start the bitbake shell, after setting all the
# required project configurations and customizations
#
################################################################################

# Defining important variables
export SHATRIX_RPI_SCRIPT_PATH=$(dirname $(readlink -e $0))
export SHATRIX_RPI_BUILD_PATH="$SHATRIX_RPI_SCRIPT_PATH/../build-rpi"
export EXTERNAL_LAYERS_PATH="$SHATRIX_RPI_SCRIPT_PATH/../external"

# make sure build dir exists
mkdir -p $SHATRIX_RPI_BUILD_PATH
cd $SHATRIX_RPI_BUILD_PATH

# source the default poky init script
. $EXTERNAL_LAYERS_PATH/poky/oe-init-build-env $SHATRIX_RPI_BUILD_PATH

# Custom Functions
function _print_info {
  echo -e "\e[34m\e[1m INFO: $1 \e[39m\e[0m"
}

function check_and_add_bblayer {
  if [ "$(grep $1 $SHATRIX_RPI_BUILD_PATH/conf/bblayers.conf)" == "" ]; then
    sed -i '/BBLAYERS ?= \" \\/a\  '$EXTERNAL_LAYERS_PATH'\/'$2' \\' $SHATRIX_RPI_BUILD_PATH/conf/bblayers.conf
    _print_info "Layer: ($1) was added successfully to bblayers.conf"
  fi
}

# include the extra project conf, and update bblayers paths
if [ "$(grep 'shatrix-rpi.conf' $SHATRIX_RPI_BUILD_PATH/conf/local.conf)" == "" ]; then
  echo 'require conf/shatrix-rpi.conf' >> $SHATRIX_RPI_BUILD_PATH/conf/local.conf
  _print_info "local.conf was updated successfully with the project extra shatrix-rpi.conf"
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
check_and_add_bblayer 'meta-shatrix-rpi' '../meta-shatrix-rpi'

# Print more help for the user
echo ""
_print_info "Please type bitbake sh-rpi-core-image"

# enter the bitbake shell
bash
