#!/usr/bin/env bash
docker build -t "docker_awscli" .
docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
docker tag docker_awscli sjsucohort6/docker_awscli
docker push sjsucohort6/docker_awscli

