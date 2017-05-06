#!/usr/bin/env bash
eval $(docker-machine env default)

docker stop kafka
docker stop zookeeper

docker stop mongodb

docker stop user-service
docker stop slackbot-service
docker stop riabot-service
docker stop chatbot-service
docker stop command-processor-service
