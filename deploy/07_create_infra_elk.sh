#!/usr/bin/env bash

eval $(docker-machine env node-1)

# Elasticsearch
docker service create \
    --name elasticsearch \
    --network proxy-net \
    --reserve-memory 300m \
    -p 9200:9200 \
    elasticsearch:2.4

docker service ps elasticsearch

# Logstash
docker service create --name logstash \
    --mount "type=bind,source=$PWD/logstash,target=/conf" \
    --network proxy-net \
    -e LOGSPOUT=ignore \
    --reserve-memory 250m \
    logstash:2.4 logstash -f ./logstash/logstash.conf

docker service ps logstash

# Check logs from logstash
LOGSTASH_NODE=$(docker service ps logstash | tail -n +2 | awk '{print $4}')

eval $(docker-machine env $LOGSTASH_NODE)

LOGSTASH_ID=$(docker ps -q --filter "ancestor=logstash:2.4")

docker logs $LOGSTASH_ID

eval $(docker-machine env node-1)

# Logspout
docker service create --name logspout \
    --network proxy-net \
    --mode global \
    --mount "type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock" \
    -e SYSLOG_FORMAT=rfc3164 \
    gliderlabs/logspout syslog://logstash:51415

docker service ps logspout

open http://$(docker-machine ip node-1):3000

# Kibana
docker service create --name kibana \
    --network elk \
    --network proxy-net \
    -e ELASTICSEARCH_URL=http://elasticsearch:9200 \
    --reserve-memory 50m \
    --label com.df.notify=true \
    --label com.df.distribute=true \
    --label com.df.servicePath=/app/kibana,/bundles,/elasticsearch \
    --label com.df.port=5601 \
    kibana:4.6

docker service ps kibana

open http://$(docker-machine ip node-1)/app/kibana

