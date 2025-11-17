################################################################################
# Shatrox Qt 5 Desktop Image (Xfce Desktop)
################################################################################

require sh-rpi-core-image.bb

DESCRIPTION = "Shatrox Qt 5 Desktop Image with Xfce and full Qt5 stack."
LICENSE = "MIT"
PR = "r003"

# === Re-enable GUI features ===
DISTRO_FEATURES:append = " x11 opengl"
DISTRO_FEATURES:remove = " wayland"

# === Desktop environment packages (Xfce) ===
DESKTOP_PKGS = " \
    packagegroup-core-x11 \
    packagegroup-xfce-base \
    packagegroup-xfce-extended \
    xf86-video-modesetting \
    xf86-input-libinput \
    xf86-input-evdev \
    ttf-bitstream-vera \
"

# === Clean Qt 5 Package List ===
QT5_PKGS = " \
    qtbase \
    qtbase-plugins \
    qtbase-tools \
    qtdeclarative \
    qtdeclarative-tools \
    qtdeclarative-qmlplugins \
    qtgraphicaleffects \
    qtquickcontrols \
    qtquickcontrols2 \
    qtimageformats \
    qtsvg \
    qtmultimedia \
    qtserialport \
    qtwebsockets \
    qtvirtualkeyboard \
    qtxmlpatterns \
"

# === Image install ===
IMAGE_INSTALL += " \
    ${DESKTOP_PKGS} \
    ${QT5_PKGS} \
"

export IMAGE_BASENAME = "sh-rpi-qt-image"
