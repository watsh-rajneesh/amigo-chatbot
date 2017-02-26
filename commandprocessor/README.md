# Command Processor
This module listens for incoming user messages on Kafka's topic named "in_msg" in a forever loop and as soon as it 
receives a message it schedules a job to process the message.