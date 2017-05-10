#!/usr/bin/env bash -x
source ~/env.sh

open -a Terminal.app ./start_kafka.sh

sleep 60

open -a Terminal.app ./start_user_service.sh

sleep 10

open -a Terminal.app ./start_slackbot_service.sh

sleep 10

open -a Terminal.app ./start_riabot_service.sh

sleep 10

open -a Terminal.app ./start_chatbot_service.sh

#sleep 10

open -a Terminal.app ./start_command_processor_service.sh
