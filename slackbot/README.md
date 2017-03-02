Slackbot
========

A bot for slack to receive slack messages. 

#Usage

###Build docker image 
docker build -t="slackbot" .

or pull from dockerhub:

docker pull sjsucohort6/slackbot

###Run the slackbot
docker run -it --rm -e SLACK_BOT_TOKEN='<bot api token>' -e BOT_ID='<bot id>' sjsucohort6/slackbot 
