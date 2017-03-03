# Command Processor
This module listens for incoming user messages on Kafka's topic named "user_msg" in a forever loop and as soon as it 
receives a message it schedules a job to process the message.

# Build Docker Image
```$ docker build -t="command-processor-service" .```

or pull from dockerhub:

```$ docker pull sjsucohort6/command-processor-service```

###Run the slackbot
```$ docker run -it --rm -e AUTH_EMAIL='<docker hub email>' \
-e AUTH_USERNAME='<docker hub user>' \
-e AUTH_PASSWORD='<docker hub passwd>' \
-e AWS_DEFAULT_REGION='<aws region>' \
-e AWS_ACCESS_KEY_ID='<aws access key id>' \
-e AWS_SECRET_ACCESS_KEY='<aws secret access key>' \
sjsucohort6/command-processor-service``` 
