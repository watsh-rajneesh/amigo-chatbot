#!/usr/bin/env bash

# Assuming that the deploy_local.sh is run and the node-1 exists.
eval $(docker-machine env node-1)

# check proxy config
NODE=$(docker service ps proxy | tail -n +2 | awk '{print $4}')

eval $(docker-machine env $NODE)

ID="$(docker ps -q \
    --filter label=com.docker.swarm.service.name=proxy)"

docker exec -it \
    $ID cat /cfg/haproxy.cfg

# Check the user-service is configured

ID="$(docker ps -q --filter label=com.docker.swarm.service.name=util)"

echo "Util Container's ID: $ID"

docker exec -it $ID apk add --update drill
docker exec -it $ID ping -c 4 swarm-listener
docker exec -it $ID ping -c 4 proxy
docker exec -it $ID drill user-service
docker exec -it $ID drill user-db

NODE1_IP="$(docker-machine ip node-1)"
echo $NODE1_IP

# Create a user
curl -X POST \
  http://$NODE1_IP/api/v1.0/users \
  -H 'content-type: application/json' \
  -d '{
	"email": "watsh.rajneesh@sjsu.edu",
	"name": "watsh",
	"password":"pass",
	"slackUser":"watsh",
	"riaId": "1",
	"awsCredentials": {
		"region": "us-west-2",
		"awsAccessKeyId": "secretKeyId",
		"awsSecretAccessKey": "secretAccessKey"
	}
}'


# Check that we can execute REST calls via any of the swarm nodes
# Get all users
curl -X GET -u watsh.rajneesh@sjsu.edu:pass \
  http://$(docker-machine ip node-1)/api/v1.0/users \
  -H 'content-type: application/json'


curl -X GET -u watsh.rajneesh@sjsu.edu:pass \
  http://$(docker-machine ip node-3)/api/v1.0/users \
  -H 'content-type: application/json'
