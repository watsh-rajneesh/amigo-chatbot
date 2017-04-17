#!/usr/bin/env bash

# Setup infrastructure ...
source ./01_create_infra_swarm_cluster.sh
# sleep 60 secs
sleep 60

# Create overlay networks
source ./02_create_infra_overlay_networks.sh
sleep 60

# Create Docker Flow Proxy Service
source ./03_create_infra_proxy_service.sh

# TODO Create and use local Docker registry
#source ./create_infra_docker_registry.sh
#sleep 60

# FIXME change to use local docker registry
#source ./06_cideploy.sh

# Logging services - ELK stack setup
#source ./07_create_infra_elk.sh
#docker stack deploy --compose-file composefiles/docker-compose-logging.yml logging

# Monitoring services setup
#source ./08_create_infra_monitoring.sh
#docker stack deploy --compose-file composefiles/docker-compose-monitoring.yml monitoring




# Add application micro services ...
source ./04_create_app_user_service.sh
sleep 60





# Add health checks and diagnostics ...
source ./05_create_util_diag_service.sh

