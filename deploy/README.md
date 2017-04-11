# Docker Swarm Setup

For development setup, docker swarm cluster is created on localhost using virtualbox driver.

Please note:

If you see this error - https://github.com/docker/kitematic/issues/1193

Remove all host only interfaces from my virtualbox 
(VirtualBox => Preferences => Network => Host-only networks), 
then delete ~/.docker/ folder 
and exit virtualbox. Launch virtualbox GUI and ensure there is no error. Delete VMs if you dont need or
execute undeploy_local.sh.

# Usage

## Deploy
1. Swarm cluster with 3 nodes (1 manager and 2 workers)
2. Create 2 overlay networks:
    1. user-net
    2. proxy-net
3. Create the following services:
    1. user-db
    2. util
    3. swarm-listener
    4. proxy
    5. user-service

```bash
chmod +x ./deploy.sh
./deploy.sh
```


## Undeploy

Will delete the swarm nodes.

```bash
chmod +x ./undeploy.sh
./undeploy.sh
```

## Diagnostics

Will use the util service created during deployment to run diagnostics like:
1. ping or drill all services
2. create a user using /users endpoint of user-service
3. get all users from user-service via 2 different swarm nodes and get same result

