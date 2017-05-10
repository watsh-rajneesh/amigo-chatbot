#!/usr/bin/env bash

eval $(docker-machine env node-1)
# run unit tests
# build docker image
#cd /tmp
#git clone https://github.com/sjsucohort6/amigo-chatbot.git
cd ../user-service

export HOST_IP=localhost

docker-compose \
    -f docker-compose-test-local.yml \
    run --rm unit

docker-compose \
    -f docker-compose-test-local.yml \
    build user-service

docker-compose \
    -f docker-compose-test-local.yml \
    down

docker tag user-service localhost:5000/user-service:1.0
docker push localhost:5000/user-service:1.0

docker-compose \
    -f docker-compose-test-local.yml \
    down

#cd ..
# build entire project in one shot
#docker run -it --rm -v "$PWD":/app -w /app sjsucohort6/jdk8-maven:3.3 mvn clean install -DskipTests
# create the docker image with latest bits and push to docker hub
#cd user-service
#source ./run.sh

cd ../deploy