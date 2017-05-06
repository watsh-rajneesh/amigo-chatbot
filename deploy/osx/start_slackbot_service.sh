#!/usr/bin/env bash -x

source ~/env.sh
cd ${AMIGO_SRC}/slackbot-service

mvn clean install -DskipTests
# Set up environment variable for accessing chatbot service
export PROXY_HOST_NAME="localhost:9090"

java -jar target/slackbot-service-1.0-SNAPSHOT.jar