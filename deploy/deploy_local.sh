#!/usr/bin/env bash

# Setup infrastructure ...
source ./01_create_swarm_cluster.sh
# sleep 60 secs
sleep 60

source ./02_create_overlay_networks.sh
sleep 60

source ./03_create_proxy_service.sh
sleep 60


# Add application micro services ...
source ./04_create_user_service.sh
sleep 60

# Add health checks and diagnostics ...
source ./05_create_util_diag_service.sh

