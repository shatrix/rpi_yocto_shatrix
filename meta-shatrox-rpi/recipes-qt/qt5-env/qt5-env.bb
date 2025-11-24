SUMMARY = "Install qt5-env.sh script to start qt apps"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://qt5-env.sh"

PR = "r1"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 qt5-env.sh ${D}${sysconfdir}/profile.d/qt5-env.sh
}

FILES_${PN} = "${sysconfdir}"

