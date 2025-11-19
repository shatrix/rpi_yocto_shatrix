################################################################################
#
# Shatrox Qt 5 Desktop Image (Xfce Desktop)
#
# Features:
# - Boots to Xfce Desktop (GUI)
# - Login Manager: LXDM
# - Full Qt 5 Support
#
################################################################################

require sh-rpi-core-image.bb

DESCRIPTION = "Shatrox Qt 5 Desktop Image with Xfce and full Qt5 stack."
LICENSE     = "MIT"
PR          = "r002"

# 1. ENABLE X11 IMAGE FEATURES
IMAGE_FEATURES += " \
    x11-base \
    splash \
    hwcodecs \
"

# 2. GRAPHICS DRIVERS (RPi 5)
GRAPHICS_PKGS = " \
    mesa \
    mesa-demos \
    libgbm \
    xf86-video-modesetting \
    xf86-input-libinput \
    xf86-input-evdev \
"

# 3. DESKTOP ENVIRONMENT (Xfce + LXDM)
# Switched to lxdm because lightdm is missing in Scarthgap meta-oe.
DESKTOP_PKGS = " \
    packagegroup-core-x11 \
    packagegroup-xfce-base \
    packagegroup-xfce-extended \
    lxdm \
    ttf-dejavu-sans \
    ttf-dejavu-sans-mono \
    ttf-bitstream-vera \
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
    ${DESKTOP_PKGS} \
    ${QT5_PKGS} \
"

# 6. POST-PROCESS: Auto-login Configuration for LXDM (Optional but Recommended)
# This sets the default user 'root' to auto-login. Change 'root' to your user if needed.
configure_lxdm_autologin() {
    sed -i 's/# autologin=.*/autologin=root/' ${IMAGE_ROOTFS}/etc/lxdm/lxdm.conf
}

ROOTFS_POSTPROCESS_COMMAND += "configure_lxdm_autologin; "

export IMAGE_BASENAME = "sh-rpi-qt-image"
