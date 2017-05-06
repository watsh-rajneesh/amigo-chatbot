#!/usr/bin/env bash

# Function copied from http://stackoverflow.com/questions/989349/running-a-command-in-a-new-mac-os-x-terminal-window
function runcmd() {
   declare args
   # escape single & double quotes
   args="${@//\'/\'}"
   args="${args//\"/\\\"}"
   printf "%s" "${args}" | /usr/bin/pbcopy
   #printf "%q" "${args}" | /usr/bin/pbcopy
   /usr/bin/open -a Terminal
   /usr/bin/osascript -e 'tell application "Terminal" to do script with command "/usr/bin/clear; cd \"`pwd`\"; eval \"$(/usr/bin/pbpaste)\"; $1;"'
   return 0
}

source ~/env.sh
export AMIGO_SRC=~/files/git/amigo-chatbot

# Start Kafka
function start_kafka() {
    runcmd "cd ${AMIGO_SRC}/docker-images/kafka && docker-compose -f ./docker-compose-single-broker.yml up -d"
}


# Assuming mongodb is running

function start_user_service() {
    runcmd "cd ${AMIGO_SRC}/user-service; mvn clean install -DskipTests; javadebug -jar target/user-service-1.0-SNAPSHOT.jar server config.yml"
}

function start_slackbot_service() {
    runcmd "cd ${AMIGO_SRC}/slackbot-service; mvn clean install -DskipTests; javadebug -jar target/slackbot-service-1.0-SNAPSHOT.jar"
}

function start_riabot_service() {
    runcmd "cd ${AMIGO_SRC}/riabot-service; mvn clean install -DskipTests; javadebug -jar target/riabot-service-1.0-SNAPSHOT.jar server config.yml"
}

function start_chatbot_service() {
    runcmd "cd ${AMIGO_SRC}/chatbot-service; mvn clean install -DskipTests; javadebug -jar target/chatbot-service-1.0-SNAPSHOT.jar server config.yml"
}

function start_command_processor() {
    runcmd "cd ${AMIGO_SRC}/command-processor-service; mvn clean install -DskipTests; javadebug -jar target/command-processor-service-1.0-SNAPSHOT.jar server config.yml"
}

start_kafka && start_user_service && start_chatbot_service && start_slackbot_service && start_riabot_service && start_command_processor
