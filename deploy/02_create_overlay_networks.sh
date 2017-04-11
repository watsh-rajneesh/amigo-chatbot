#!/usr/bin/env bash

eval $(docker-machine env node-1)

# Create user-net overlay network
docker network create --driver overlay user-net

# Create a proxy overlay network
docker network create --driver overlay proxy-net

echo "Created overlay networks"