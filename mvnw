#!/bin/sh
# Maven Wrapper script
# Downloads Maven 3.9.6 automatically if not present

MAVEN_VERSION="3.9.6"
MAVEN_DIR="$HOME/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_BIN="${MAVEN_DIR}/bin/mvn"

if [ ! -f "$MAVEN_BIN" ]; then
    echo "Descargando Maven ${MAVEN_VERSION}..."
    mkdir -p "$MAVEN_DIR"
    MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
    curl -fsSL "$MAVEN_URL" | tar -xz -C "$HOME/.m2/wrapper/dists/" 2>/dev/null || \
    wget -qO- "$MAVEN_URL" | tar -xz -C "$HOME/.m2/wrapper/dists/" 2>/dev/null
    if [ ! -f "$HOME/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}/bin/mvn" ]; then
        echo "Error: no se pudo descargar Maven. Instala Maven manualmente con:"
        echo "  sudo apt-get install maven"
        exit 1
    fi
fi

exec "$MAVEN_BIN" "$@"
