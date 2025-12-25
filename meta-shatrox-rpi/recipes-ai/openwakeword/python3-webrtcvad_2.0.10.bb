SUMMARY = "Python interface to the Google WebRTC Voice Activity Detector"
DESCRIPTION = "A Python interface to the WebRTC Voice Activity Detector"
HOMEPAGE = "https://github.com/wiseman/py-webrtcvad"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=9510fa348d7f3a274ad388ad74980928"

SRC_URI = "https://files.pythonhosted.org/packages/89/34/91e32e08f461a6d950c8add7e56c7a9e4c8f28d30e8b5622cf7cfb54aa39/webrtcvad-2.0.10.tar.gz"
SRC_URI[sha256sum] = "f1bed2fb25b63fb7b1a55d64090c993c9c9167b28485ae0bcdd81cf6ede96aea"

S = "${WORKDIR}/webrtcvad-2.0.10"

inherit setuptools3

RDEPENDS:${PN} = "python3-core"
