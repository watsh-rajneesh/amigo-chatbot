#!/usr/bin/env bash
# start with 1 kafka and 1 zookeeper containers
docker-compose up -d

# start a shell into the container
./start-kafka-shell.sh 192.168.86.89 192.168.86.89:2181

# create user_msg topic
#$KAFKA_HOME/bin/kafka-topics.sh --create --topic user.msg \
#--partitions 4 --zookeeper $ZK --replication-factor 1

# display the topic is created
$KAFKA_HOME/bin/kafka-topics.sh --describe --topic user.msg --zookeeper $ZK
