#!/usr/bin/env bash

# This script is to run the services in docker containers on
# the local docker machine and not in swarm cluster.
# Local run assumes mongodb is installed locally and is shared for
# both command DB and user DB.
#
# To run locally in docker swarm cluster use deploy_local.sh.
# To run in aws docker swarm cluster use deploy_aws.sh

# Assumes default docker machine is created. If not created,
# then create with command:
# docker-machine create default

# Create env.sh in home directory with the following:
# export SLACK_BOT_TOKEN=
# export BOT_ID=
# export WIT_AI_SERVER_ACCESS_TOKEN=
# export DOCKER_USERNAME=
# export DOCKER_PASSWORD=

source ~/env.sh

echo "Do you want to build docker images as part of this run? [y/N]:"
read BUILD_IMAGES
echo $BUILD_IMAGES

if [ -z $BUILD_IMAGES ]; then
    export BUILD_IMAGES='N'
fi

echo $BUILD_IMAGES

eval $(docker-machine env default)

echo "Docker host ip is: $(docker-machine ip default)"

# Start kafka MQ
pushd .
cd ../docker-images/kafka
docker-compose -f ./docker-compose-single-broker.yml up -d
popd

# Start mongo DB
docker run -d --rm --name mongodb \
    sjsucohort6/mongodb:latest

# Sleep for 2 mins so the above services get enough time to startup
sleep 120

# Start user-service
if [ $BUILD_IMAGES = 'y' ] || [ $BUILD_IMAGES = 'Y' ]; then
    pushd .
    cd ../user-service
    source ./build.sh
    popd
fi

docker run -d --rm \
    -p 8080:8080 \
    -e DB="$(docker-machine ip default)" \
    --name user-service sjsucohort6/user-service:1.0

# Start chatbot-service
#./macterm.sh 'cd ../chatbot-service; java -jar target/chatbot-service-1.0-SNAPSHOT.jar server config.yml'
if [ $BUILD_IMAGES = 'y' ] || [ $BUILD_IMAGES = 'Y' ]; then
    pushd .
    cd ../chatbot-service
    source ./build.sh
    popd
fi

docker run -d --rm -e KAFKA_HOST_NAME="$(docker-machine ip default)"  \
    -p 9090:8080 \
    --name chatbot-service sjsucohort6/chatbot-service:1.0


# Start slackbot-service
if [ $BUILD_IMAGES = 'y' ] || [ $BUILD_IMAGES = 'Y' ]; then
    pushd .
    cd ../slackbot-service
    source ./build.sh
    popd
fi

docker run -d --rm -e SLACK_BOT_TOKEN="$SLACK_BOT_TOKEN" \
 -e BOT_ID="$BOT_ID" \
 -e PROXY_HOST_NAME="$(docker-machine ip default):9090" \
 -e WIT_AI_SERVER_ACCESS_TOKEN="$WIT_AI_SERVER_ACCESS_TOKEN" \
 --name slackbot-service sjsucohort6/slackbot-service:1.0



# Start riabot-service
if [ $BUILD_IMAGES = 'y' ] || [ $BUILD_IMAGES = 'Y' ]; then
    pushd .
    cd ../riabot-service
    source ./build.sh
    popd
fi

docker run -d --rm -e PROXY_HOST_NAME="$(docker-machine ip default):9090" \
    -p 7070:8080 \
    --name riabot-service sjsucohort6/riabot-service:1.0


# Start command-processor-service
#./macterm.sh 'cd ../command-processor-service; java -jar target/command-processor-service-1.0-SNAPSHOT.jar server config.yml'
if [ $BUILD_IMAGES = 'y' ] || [ $BUILD_IMAGES = 'Y' ]; then
    pushd .
    cd ../command-processor-service
    source ./build.sh
    popd
fi

docker run -d --rm -e KAFKA_HOST_NAME="$(docker-machine ip default)" \
    -e DB="$(docker-machine ip default)" \
    -p 6060:8080 \
    --name command-processor-service sjsucohort6/command-processor-service:1.0

