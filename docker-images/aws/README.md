# docker_awscli
Dockerfile for docker_awscli image.

Based on - https://lostechies.com/gabrielschenker/2016/09/21/easing-the-use-of-the-aws-cli/:

# Build Image from Dockerfile
Run following to build the container image with awscli.

sudo docker build -t "sjsucohort6/docker_awscli" .

# Execute awscli commands
sudo docker run -it --rm -e AWS_DEFAULT_REGION='<your region>' -e AWS_ACCESS_KEY_ID='<your access key>' -e AWS_SECRET_ACCESS_KEY='<your secret access key>' --entrypoint aws sjsucohort6/docker_awscli:latest ecs list-clusters

or just define alias as:

alias aws=sudo docker run -it --rm -e AWS_DEFAULT_REGION='<your region>' -e AWS_ACCESS_KEY_ID='<your access key>' -e AWS_SECRET_ACCESS_KEY='<your secret access key>' --entrypoint aws sjsucohort6/docker_awscli:latest

Then run awscli commands without having awscli installed locally.
aws s3 ls

# Image on docker hub
To pull from docker hub:
docker pull sjsucohort6/docker_awscli


@see https://github.com/xueshanf/docker-awscli/blob/master/Dockerfile