#!/usr/bin/env bash

# Assumes maven is installed locally
#mvn clean package -DskipTests

# build a new image
#docker build -t="command-processor-service" .

# push the image to docker hub
#docker login
#docker tag command-processor-service sjsucohort6/command-processor-service
#docker push sjsucohort6/command-processor-service

# execute the command processor
docker run -d -e AWS_DEFAULT_REGION='us-west-2' \
                    -e AWS_ACCESS_KEY_ID='AKIAINY7I3V5SMK7GK4Q' \
                    -e AWS_SECRET_ACCESS_KEY='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' \
                    -e AUTH_EMAIL='watsh.rajneesh@sjsu.edu' \
                    -e AUTH_USERNAME='sjsucohort6' \
                    -e AUTH_PASSWORD='Rushil123!' \
                    --link kafka
                    --name command-processor-service sjsucohort6/command-processor-service