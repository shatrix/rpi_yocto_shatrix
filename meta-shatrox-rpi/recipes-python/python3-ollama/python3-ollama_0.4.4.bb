SUMMARY = "Ollama Python library"
HOMEPAGE = "https://github.com/ollama/ollama-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8abe7311c869aba169d640cf367a4af"

PYPI_PACKAGE = "ollama"

SRC_URI[sha256sum] = "e1db064273c739babc2dde9ea84029c4a43415354741b6c50939ddd3dd0f7ffb"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-poetry-core-native"
RDEPENDS:${PN} += "python3-httpx python3-json python3-pydantic"

BBCLASSEXTEND = "native nativesdk"
