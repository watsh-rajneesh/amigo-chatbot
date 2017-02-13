Based on - https://lostechies.com/gabrielschenker/2016/09/21/easing-the-use-of-the-aws-cli/:

Run following to build the container image with awscli.

sudo docker build -t="sjsucohort6/docker_awscli" .

sudo docker images
REPOSITORY                  TAG                 IMAGE ID            CREATED             SIZE
sjsucohort6/docker_awscli   latest              5dd4b2f1ac5c        2 minutes ago       720 MB

sudo docker run -it --rm -e AWS_DEFAULT_REGION='<your region>' -e AWS_ACCESS_KEY_ID='<your access key>' -e AWS_SECRET_ACCESS_KEY='<your secret access key>' --entrypoint aws sjsucohort6/docker_awscli:latest ecs list-clusters


or just define alias as:

alias aws=sudo docker run -it --rm -e AWS_DEFAULT_REGION='<your region>' -e AWS_ACCESS_KEY_ID='<your access key>' -e AWS_SECRET_ACCESS_KEY='<your secret access key>' --entrypoint aws sjsucohort6/docker_awscli:latest

Then run awscli commands without having awscli installed locally.
aws s3 ls


To pull from docker hub:
docker pull sjsucohort6/docker_awscli


