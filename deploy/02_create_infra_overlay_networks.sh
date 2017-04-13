#!/usr/bin/env bash

eval $(docker-machine env node-1)

# Create user-net overlay network
docker network create --driver overlay user-net

# Create a proxy overlay network
docker network create --driver overlay proxy-net

# Create an overlay network for ELK stack for capturing service logging
#docker network create --driver overlay elk

echo "Created overlay networks"