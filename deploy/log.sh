#!/usr/bin/env bash

##################
# View logs of the services deployed in docker swarm.
#
# Usage:
# ./log.sh <service name>
#
#################

NODE=$(docker service ps $1 | tail -n +2 | awk '{print $4}')

eval $(docker-machine env $NODE)

ID="$(docker ps -q \
    --filter label=com.docker.swarm.service.name=$1)"

echo "ID: $ID"

docker logs --tail 100 $ID
