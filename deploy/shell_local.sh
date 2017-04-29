#!/usr/bin/env bash

eval $(docker-machine env default)

if [ -z $1 ]; then
 echo "Please enter service name to shell into"
 echo "Usgae: ./shell_local.sh <service-name>"
fi


docker exec -it $1 /bin/sh