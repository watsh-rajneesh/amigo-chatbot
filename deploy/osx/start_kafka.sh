#!/usr/bin/env bash -x

source ~/env.sh

cd ${AMIGO_SRC}/docker-images/kafka

docker-compose -f ./docker-compose-single-broker.yml up -d