[![Run Status](https://api.shippable.com/projects/58b94166ac9c0b0600a8b21c/badge?branch=master)](https://app.shippable.com/projects/58b94166ac9c0b0600a8b21c) [![Code Climate](https://codeclimate.com/github/sjsucohort6/amigo-chatbot/badges/gpa.svg)](https://codeclimate.com/github/sjsucohort6/amigo-chatbot) [![Test Coverage](https://codeclimate.com/github/sjsucohort6/amigo-chatbot/badges/coverage.svg)](https://codeclimate.com/github/sjsucohort6/amigo-chatbot/coverage) [![Issue Count](https://codeclimate.com/github/sjsucohort6/amigo-chatbot/badges/issue_count.svg)](https://codeclimate.com/github/sjsucohort6/amigo-chatbot) [![Build Status](https://travis-ci.org/sjsucohort6/amigo-chatbot.svg?branch=master)](https://travis-ci.org/sjsucohort6/amigo-chatbot) [![Documentation Status](http://readthedocs.org/projects/amigo-chatbot/badge/?version=latest)](http://amigo-chatbot.readthedocs.io/en/latest/?badge=latest) 
# amigo-chatbot
A chatbot for cloud operations management.

![Amigo Image](amigo.png "A chatbot for cloud operations management.")
Image clipart of Amigo is courtsey free image published on [this site](http://vectorcharacters.net/robot-vector-characters/cute-vector-robot-character).

Read the workbook - [Amigo Project Report (Draft)](https://sjsu0-my.sharepoint.com/personal/watsh_rajneesh_sjsu_edu/_layouts/15/guestaccess.aspx?docid=0a9a2d9d25a994c66bd1f153c354c1b1b&authkey=AVotnLN4G8fSA1cmlwb6RLU)

# Features
1. Chatbot service with adapters for: 
   * Slack messenger (a slackbot), 
   * a web UI, 
   * Raspberry Pi Intelligent Agent (RIA) and 
   * mobile app.
2. Highly scalable, designed as set of dockerized microservices deployed in Docker Swarm mode cluster.
3. Production deployment in AWS 
4. Can support operations management for various cloud providers. AWS is supported for now.
5. Demo use case of cloud ops on AWS public cloud - enable user to use the chatbot service to perform devops in a  
collaborative environment of enterprise messenger - [DevOps Functional Specification](https://1drv.ms/b/s!AnGP6YY6ad9FgYN1jLX4QRvzE--Ybw). 

Amigo chatbot enables user to be able to type in any aws-cli command within Slack that user can execute in terminal. 

When user receives an event from Amazon Cloud Watch that a node has high CPU usage (beyond a certain thresold) then this event can be seen within Slack messenger via the AWS Cloud Watch bot. But in order to remedy the situation user can add another node or scale out the node. To do any of this user will need to either use AWS console or AWS CLI from a terminal and leave the Slack messenger for that. That is where Amigo chatbot comes in. It enables user to execute any command that is supported by aws-cli from within slack. For some commands like show me my ec2 zones, the intent of the message is inferred from the message and the corresponding awscli command is executed (aws ec2 describe-availability-zones). But user can always type in the actual command instead and Amigo chatbot will execute that and show the result.

A [demo video for the bot is availabe on youtube](https://youtu.be/qTltmTgN1Ws)

We also participated in [AWS Chatbot Hackathon](https://devpost.com/software/amigo-chatbot)
