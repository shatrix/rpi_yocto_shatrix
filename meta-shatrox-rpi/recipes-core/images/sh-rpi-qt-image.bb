################################################################################
#
# Shatrox Qt 5 Desktop Image
#
# Features:
# - Full Qt 5 Support
#
################################################################################

require sh-rpi-core-image.bb

DESCRIPTION = "Shatrox Qt 5 Desktop Image with Xfce and full Qt5 stack."
LICENSE     = "MIT"
PR          = "r002"

IMAGE_FEATURES += " \
    splash \
    hwcodecs \
    ssh-server-openssh \
"

# 2. GRAPHICS DRIVERS (RPi 5)
GRAPHICS_PKGS = " \
    mesa \
    mesa-demos \
    libgbm \
"

# 4. QT 5 PACKAGES
QT5_PKGS = " \
    qtbase \
    qtbase-plugins \
    qtbase-tools \
    qtbase-examples \
    qtdeclarative \
    qtdeclarative-tools \
    qtdeclarative-qmlplugins \
    qtgraphicaleffects \
    qtquickcontrols \
    qtquickcontrols2 \
    qtimageformats \
    qtsvg \
    qtmultimedia \
    qtmultimedia-plugins \
    qtserialport \
    qtwebsockets \
    qtvirtualkeyboard \
    qtxmlpatterns \
"

# 5. INSTALLATION
IMAGE_INSTALL += " \
    ${GRAPHICS_PKGS} \
    ${QT5_PKGS} \
"

export IMAGE_BASENAME = "sh-rpi-qt-image"
