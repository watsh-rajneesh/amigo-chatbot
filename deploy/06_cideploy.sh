#!/usr/bin/env bash

# Assumption is that mvn deploy will build source, test source, build docker image, tag it and push it to docker hub
# mvn deploy will iteratively do the above for all sub-modules.
#docker run -it --name cideployer sjsucohort6/cideploy:1.0 \
#/bin/bash -c "git clone https://github.com/sjsucohort6/amigo-chatbot.git; cd amigo-chatbot; mvn deploy"

#eval $(docker-machine env node-1)

#docker service create --name cideployer sjsucohort6/cideploy:1.0 \
#    /bin/bash -c "git clone https://github.com/sjsucohort6/amigo-chatbot.git; cd amigo-chatbot; mvn deploy"

pushd .

cd ../user-service

# Only local tests are run .. so in this case mongodb is running locally
mvn clean install -DskipTests
java -jar target/user-service-1.0-SNAPSHOT.jar server config.yml &
mvn test

# If the above tests succeeded then make docker image and push to docker hub
source ./run.sh

popd
