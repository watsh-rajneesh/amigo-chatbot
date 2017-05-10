#!/usr/bin/env bash -x

source ~/env.sh
cd ${AMIGO_SRC}/command-processor-service

mvn clean install -DskipTests
export KAFKA_HOST_NAME="localhost"
export DB="localhost"

java -jar target/command-processor-service-1.0-SNAPSHOT.jar server config_dev.yml