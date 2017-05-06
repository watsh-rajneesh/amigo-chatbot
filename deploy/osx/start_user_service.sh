#!/usr/bin/env bash -x
source ~/env.sh

cd ${AMIGO_SRC}/user-service

mvn clean install -DskipTests
export DB="localhost"

java -jar target/user-service-1.0-SNAPSHOT.jar server config_dev.yml