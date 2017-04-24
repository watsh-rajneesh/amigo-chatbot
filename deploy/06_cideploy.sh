#!/usr/bin/env bash

# TODO Use mvn deploy to build source, test source, build docker image, tag it and push it to docker hub
# mvn deploy will iteratively do the above for all sub-modules.
# For now doing it with shell script located in each sub-module.

#eval $(docker-machine env node-1)

#docker service create --name cideployer sjsucohort6/cideploy:1.0 \
#    /bin/bash -c "git clone https://github.com/sjsucohort6/amigo-chatbot.git; cd amigo-chatbot; mvn deploy"

# PS: This build, test, create docker image is happening locally and not in a swarm cluster. Ideally should happen
# via Jenkins in a Swarm cluster (TODO)

pushd .

cd ../user-service

source ./build.sh

cd ../chatbot-service

source ./build.sh

cd ../slackbot-service

source ./build.sh

cd ../riabot-service

source ./build.sh

cd ../command-processor-service

source ./build.sh

popd
