#!/usr/bin/env bash
# Create swarm cluster nodes with virtualbox
for i in 1 2 3; do
  docker-machine create -d virtualbox node-$i
done


# Make node1 swarm manager
eval $(docker-machine env node-1)

docker swarm init \
  --advertise-addr $(docker-machine ip node-1)


# Get the token to join worker nodes
TOKEN=$(docker swarm join-token -q worker)

# Make nodes 2 and 3 as swarm workers
for i in 2 3; do
  eval $(docker-machine env node-$i)

  docker swarm join \
    --token $TOKEN \
    --advertise-addr $(docker-machine ip node-$i) \
    $(docker-machine ip node-1):2377
done

# Check if swarm nodes created successfully
eval $(docker-machine env node-1)

docker node ls

# Create user-net overlay network
docker network create --driver overlay user-net

# Create a proxy overlay network
docker network create --driver overlay proxy-net


# Create user-db service
docker service create --name user-db \
    --network user-net \
    mongo:3.2.10

# Create a debug util service on all nodes in swarm cluster (so global mode)
docker service create --name util \
    --network user-net \
    --network proxy-net \
    --mode global \
    alpine sleep 1000000000

docker service ps util

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

# Create user-service service
docker service create --name user-service \
  -e DB=user-db \
  --network user-net \
  --network proxy-net \
  --label com.df.notify=true \
  --label com.df.distribute=true \
  --label com.df.servicePath=/api/v1.0/users \
  --label com.df.port=8080 \
  sjsucohort6/user-service:1.0

docker service ls

# Check services
docker service ps proxy
docker service ps swarm-listener
docker service ps user-db
docker service ps user-service




