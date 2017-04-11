#!/usr/bin/env bash

eval $(docker-machine env node-1)

# For docker-flow-proxy usage, please refer http://proxy.dockerflow.com/swarm-mode-auto/
# Swarm listener service for auto reconfiguration of docker-flow-proxy service
docker service create --name swarm-listener \
    --network proxy-net \
    --mount "type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock" \
    -e DF_NOTIFY_CREATE_SERVICE_URL=http://proxy:8080/v1/docker-flow-proxy/reconfigure \
    -e DF_NOTIFY_REMOVE_SERVICE_URL=http://proxy:8080/v1/docker-flow-proxy/remove \
    --constraint 'node.role==manager' \
    vfarcic/docker-flow-swarm-listener

# Create docker-flow-proxy service which will act an API Gateway
docker service create --name proxy \
    -p 80:80 \
    -p 443:443 \
    --network proxy-net \
    -e MODE=swarm \
    -e LISTENER_ADDRESS=swarm-listener \
    vfarcic/docker-flow-proxy

# Check services
docker service ps proxy
docker service ps swarm-listener

echo "Created proxy services"