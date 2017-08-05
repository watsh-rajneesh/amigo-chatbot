#!/usr/bin/env bash -x
source ~/env.sh

cd ${AMIGO_SRC}/riabot-service

mvn clean install -DskipTests
# points to chatbot service
export PROXY_HOST_NAME="localhost:9090"

java -jar target/riabot-service-1.0-SNAPSHOT.jar server config_dev.yml