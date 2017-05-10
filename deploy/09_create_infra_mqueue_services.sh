#!/usr/bin/env bash
eval $(docker-machine env node-1)

docker service create --name zookeeper \
    --network app-net \
    --constraint 'node.role == manager' \
    -p 2181:2181 \
    wurstmeister/zookeeper


docker service create --name kafka \
    --network app-net \
    --mode global \
    -e 'KAFKA_PORT=9092' \
    -e 'KAFKA_ADVERTISED_PORT=9092' \
    -e 'KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092' \
    -e 'KAFKA_ZOOKEEPER_CONNECT=tasks.zookeeper:2181' \
    -e 'KAFKA_CREATE_TOPICS=user.msg:1:1' \
    -e "HOSTNAME_COMMAND=ip r | awk '{ ip[\$3] = \$NF } END { print ( ip[\"eth0\"] ) }'" \
    --publish '9092:9092' \
    wurstmeister/kafka

docker service ps kafka
docker service ps zookeeper

echo "Created zookeeper and kafka services"