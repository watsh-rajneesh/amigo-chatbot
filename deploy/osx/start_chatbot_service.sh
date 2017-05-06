#!/usr/bin/env bash -x

source ~/env.sh

cd ${AMIGO_SRC}/chatbot-service

mvn clean install -DskipTests

export KAFKA_HOST_NAME="localhost"

java -jar target/chatbot-service-1.0-SNAPSHOT.jar server config_dev.yml