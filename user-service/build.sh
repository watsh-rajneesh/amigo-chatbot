#!/usr/bin/env bash


# Only local tests are run .. so in this case mongodb and user service are running locally
mvn clean install -DskipTests
java -jar target/user-service-1.0-SNAPSHOT.jar server config.yml &
USER_SERVICE_PID=$!
# give some time for server to startup
sleep 60
mvn test
kill $USER_SERVICE_PID
# If the above tests succeeded then make docker image and push to docker hub
# build a new image
docker build -t="user-service" .

# push the image to docker hub
docker login -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
docker tag user-service sjsucohort6/user-service:1.0
docker push sjsucohort6/user-service:1.0


