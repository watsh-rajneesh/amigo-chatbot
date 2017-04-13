#!/usr/bin/env bash

eval $(docker-machine env node-1)

# Node Exporter Service
docker service create \
    --name node-exporter \
    --mode global \
    --network proxy-net \
    --mount "type=bind,source=/proc,target=/host/proc" \
    --mount "type=bind,source=/sys,target=/host/sys" \
    --mount "type=bind,source=/,target=/rootfs" \
    --mount "type=bind,source=/etc/hostname,target=/etc/host_hostname" \
    -e HOST_HOSTNAME=/etc/host_hostname \
    basi/node-exporter:v0.1.1 \
    -collector.procfs /host/proc \
    -collector.sysfs /host/proc \
    -collector.filesystem.ignored-mount-points "^/(sys|proc|dev|host|etc)($|/)" \
    -collector.textfile.directory /etc/node-exporter/ \
    -collectors.enabled="conntrack,diskstats,entropy,filefd,filesystem,loadavg,mdadm,meminfo,netdev,netstat,stat,textfile,time,vmstat,ipvs"


docker service ps node-exporter

sleep 120

UTIL_ID="$(docker ps -q --filter \
    label=com.docker.swarm.service.name=util)"

docker exec -it $UTIL_ID \
    apk add --update curl drill

docker exec -it $UTIL_ID \
    curl http://node-exporter:9100/metrics

# CAdvisor Service
docker service create --name cadvisor \
    -p 8080:8080 \
    --mode global \
    --network proxy-net \
    --mount "type=bind,source=/,target=/rootfs" \
    --mount "type=bind,source=/var/run,target=/var/run" \
    --mount "type=bind,source=/sys,target=/sys" \
    --mount "type=bind,source=/var/lib/docker,target=/var/lib/docker" \
    google/cadvisor:v0.24.1

docker service update \
    --publish-rm 8080 cadvisor

sleep 120
open http://$(docker-machine ip node-1):8080


UTIL_ID="$(docker ps -q --filter \
    label=com.docker.swarm.service.name=util)"

docker exec -it $UTIL_ID \
    curl http://cadvisor:8080/metrics

docker exec -it $UTIL_ID \
    drill tasks.node-exporter


# Prometheus Service
docker service create \
    --name prometheus \
    --network proxy-net \
    -p 9090:9090 \
    --mount "type=bind,source=$PWD/prometheus/prometheus.yml,target=/etc/prometheus/prometheus.yml" \
    --mount "type=bind,source=$PWD/docker/prometheus,target=/prometheus" \
    --reserve-memory 200m \
    prom/prometheus:v1.2.1

docker service ps prometheus

sleep 60
open http://$(docker-machine ip node-1):9090

# Grafana
docker service create \
    --name grafana \
    --network proxy-net \
    -p 3000:3000 \
    grafana/grafana:3.1.1

docker service ps grafana

sleep 60
open http://$(docker-machine ip node-1):3000

