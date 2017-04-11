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