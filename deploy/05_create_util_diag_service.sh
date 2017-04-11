#!/usr/bin/env bash

eval $(docker-machine env node-1)

# Create a debug util service on all nodes in swarm cluster (so global mode)
docker service create --name util \
    --network user-net \
    --network proxy-net \
    --mode global \
    alpine sleep 1000000000

docker service ps util

echo "Created util diag service"

sleep 60
source ./diag.sh
