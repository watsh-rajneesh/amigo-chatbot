#!/usr/bin/env bash -x

source ~/env.sh
cd ${AMIGO_SRC}/command-processor-service

mvn clean install -DskipTests
export KAFKA_HOST_NAME="localhost"
export DB="localhost"
# points to user service
export PROXY_HOST_NAME="localhost:8080"

java -Xdebug -agentlib:jdwp=transport=dt_socket,address=7133,server=y,suspend=n -jar target/command-processor-service-1.0-SNAPSHOT.jar server config_dev.yml
