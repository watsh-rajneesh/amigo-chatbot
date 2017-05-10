#!/usr/bin/env bash
docker build -f ./UbuntuDockerfile -t "oracle-java" .
docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
docker tag user-service sjsucohort6/oracle-java:8
docker push sjsucohort6/oracle-java:8
