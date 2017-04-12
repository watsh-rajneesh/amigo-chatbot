#!/usr/bin/env bash

# Assumes maven is installed locally
#mvn clean install -DskipTests

# build a new image
docker build -t="user-service" .

# push the image to docker hub
docker login
docker tag user-service sjsucohort6/user-service:1.0
docker push sjsucohort6/user-service:1.0


