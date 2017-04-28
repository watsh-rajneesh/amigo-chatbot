#!/usr/bin/env bash

eval $(docker-machine env node-1)

# Create user-db service
docker service create --name user-db \
    --network app-net \
    --log-driver=gelf \
    --log-opt gelf-address=udp://127.0.0.1:12201 \
    --log-opt tag="user-db" \
    mongo:3.2.10


# Create user-service service
docker service create --name user-service \
  -e DB=user-db \
  --network app-net \
  --network proxy-net \
  --label com.df.notify=true \
  --label com.df.distribute=true \
  --label com.df.servicePath=/api/v1.0/users \
  --label com.df.port=8080 \
  --log-driver=gelf \
  --log-opt gelf-address=udp://127.0.0.1:12201 \
  --log-opt tag="user-service" \
  sjsucohort6/user-service:1.0

docker service ps user-db
docker service ps user-service

echo "Created user service and user db"

# Create cmd-db service
docker service create --name cmd-db \
    --network app-net \
    --log-driver=gelf \
    --log-opt gelf-address=udp://127.0.0.1:12201 \
    --log-opt tag="cmd-db" \
    mongo:3.2.10


# Create command-processor-service
docker service create --name command-processor-service \
  -e KAFKA_HOST_NAME="kafka" \
  -e DB=cmd-db \
  --network app-net \
  --network proxy-net \
  --label com.df.notify=true \
  --label com.df.distribute=true \
  --label com.df.servicePath=/api/v1.0/requests \
  --label com.df.port=8080 \
  --log-driver=gelf \
  --log-opt gelf-address=udp://127.0.0.1:12201 \
  --log-opt tag="command-processor-service" \
  sjsucohort6/command-processor-service:1.0

docker service ps cmd-db
docker service ps command-processor-service

echo "Created command processor service and cmd db"

# Create slackbot-service
# This will be directly accessible on port 9090 of the swarm nodes.
docker service create --name slackbot-service \
  -e SLACK_BOT_TOKEN="$SLACK_BOT_TOKEN" \
  -e BOT_ID="$BOT_ID" \
  -e WIT_AI_SERVER_ACCESS_TOKEN="$WIT_AI_SERVER_ACCESS_TOKEN" \
  -e PROXY_HOST_NAME="proxy" \
  --network proxy-net \
  --network app-net \
  --log-driver=gelf \
  --log-opt gelf-address=udp://127.0.0.1:12201 \
  --log-opt tag="slackbot-service" \
  sjsucohort6/slackbot-service:1.0

docker service ps slackbot-service

echo "Created slackbot service"

# Create chatbot-service
docker service create --name chatbot-service \
  -e KAFKA_HOST_NAME="kafka" \
  --network app-net \
  --network proxy-net \
  --label com.df.notify=true \
  --label com.df.distribute=true \
  --label com.df.servicePath=/api/v1.0/chat \
  --label com.df.port=8080 \
  --log-driver=gelf \
  --log-opt gelf-address=udp://127.0.0.1:12201 \
  --log-opt tag="chatbot-service" \
  sjsucohort6/chatbot-service:1.0

docker service ps chatbot-service

echo "Created chatbot service"

# Create riabot-service
docker service create --name riabot-service \
  -e PROXY_HOST_NAME="proxy" \
  --network app-net \
  --network proxy-net \
  --label com.df.notify=true \
  --label com.df.distribute=true \
  --label com.df.servicePath=/api/v1.0/ria \
  --label com.df.port=8080 \
  --log-driver=gelf \
  --log-opt gelf-address=udp://127.0.0.1:12201 \
  --log-opt tag="riabot-service" \
  sjsucohort6/riabot-service:1.0

docker service ps riabot-service

echo "Created riabot service"