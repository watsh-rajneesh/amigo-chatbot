#!/usr/bin/env bash

# Assuming that the deploy_local.sh is run and the node-1 exists.

eval $(docker-machine env node-1)

ID="$(docker ps -q --filter label=com.docker.swarm.service.name=util)"
docker exec -it $ID /bin/sh
