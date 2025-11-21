SUMMARY = "ALSA configuration for Raspberry Pi"
DESCRIPTION = "Provides /etc/asound.conf for proper ALSA audio device configuration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://asound.conf"

S = "${WORKDIR}"

do_install() {
    # Install ALSA configuration
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/asound.conf ${D}${sysconfdir}/asound.conf
}

FILES:${PN} = "${sysconfdir}/asound.conf"

RDEPENDS:${PN} = "alsa-lib"
CONFFILES:${PN} = "${sysconfdir}/asound.conf"

# Replace alsa-state package to avoid conflict over /etc/asound.conf
RPROVIDES:${PN} = "alsa-state"
RCONFLICTS:${PN} = "alsa-state"
RREPLACES:${PN} = "alsa-state"
