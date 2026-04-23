#!/bin/bash
set -e
export JAVA_HOME=/root/.local/state/mise/installs/java/21.0.2
chmod +x mvnw
./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install -Pproduction
