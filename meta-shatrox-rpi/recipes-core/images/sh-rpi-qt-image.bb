################################################################################
#
# Shatrox Qt Image, with dev tools
#
################################################################################

require sh-rpi-core-image.bb

DESCRIPTION = "Shatrox Qt Image, with dev tools"
HOMEPAGE    = "https://github.com/shatrix/rpi_yocto_shatrix"
SECTION     = "image"
PR          = "r001"

IMAGE_INSTALL += " \
  ${QT5_PKGS} \
  ${QT_EXAMPLES} \
"

QT5_PKGS = " \
  qtbase \
  qtbase-plugins \
  qt3d \
  qtcharts \
  qtdeclarative \
  qtserialport \
  qtdeclarative-tools \
  qtdeclarative-qmlplugins \
  qtconnectivity \
  qtgraphicaleffects \
  qtimageformats \
  qtlocation \
  qtquickcontrols \
  qtsensors \
  qtsensors-plugins \
  qtsystems \
  qtmultimedia \
  qtserialbus \
  qtsvg \
  qttools \
  qtscript \
  qtquickcontrols2 \
  qttranslations \
  qttranslations-qtbase \
  qttranslations-qtdeclarative \
  qttranslations-qtconnectivity \
  qttranslations-qtlocation \
  qttranslations-qtmultimedia \
  qttranslations-qtquickcontrols \
  qttranslations-qtserialport \
  qttranslations-qtwebsockets \
  qttranslations-qtxmlpatterns \
  qtwebsockets \
  qtwebsockets-qmlplugins \
  qtwebchannel \
  qtxmlpatterns \
  qtwayland \
  qtbase-tools \
  qtwebchannel-qmlplugins \
  qtvirtualkeyboard \
  tslib \
  tslib-conf \
  tslib-calibrate \
  tslib-tests \
  ttf-bitstream-vera \
  qt5-env \
"

QT_EXAMPLES = " \
  cinematicexperience \
  qt5everywheredemo \
  qt5ledscreen \
  qt5nmapcarousedemo \
  qt5nmapper \
  qt5-opengles2-test \
  qtsmarthome \
  quitbattery \
  quitindicators \
  qt5-demo-extrafiles \
"

export IMAGE_BASENAME = "sh-rpi-qt-image"
