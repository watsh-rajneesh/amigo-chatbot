#!/usr/bin/env bash
docker build -t "docker_awscli" .
docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
docker tag user-service sjsucohort6/docker_awscli:latest
docker push sjsucohort6/docker_awscli:latest
