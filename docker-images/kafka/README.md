## Tutorial
[http://wurstmeister.github.io/kafka-docker/](http://wurstmeister.github.io/kafka-docker/)

## Reference
https://github.com/wurstmeister/kafka-docker

This is cloned from the above public github repo.

## Usage
0. Setup:
Edit the docker-compose.yml to provide KAFKA_ADVERTISED_HOST_NAME and KAFKA_ZOOKEEPER_CONNECT host ips.
This is the IP of the host on which we are going to run the docker containers for Kafka and Zookeeper.

```
KAFKA_ADVERTISED_HOST_NAME: 192.168.86.89
KAFKA_ZOOKEEPER_CONNECT: 192.168.86.89:2181
```
1. Start a cluster in detached mode:
```docker-compose up -d```

It will start 2 containers:
kafkadocker_kafka_1 - with kafka running at 9092 mapped to 9092 of localhost
kafkadocker_zookeeper_1 - with zookeeper running at 2181 mapped to 2181 of localhost

To start a cluster with 2 brokers:
```docker-compose scale kafka=2```

 You can use docker-compose ps to show the running instances. 
 If you want to add more Kafka brokers simply increase the value passed to 
 docker-compose scale kafka=n
 
 If you want to customise any Kafka parameters, simply add them as environment variables 
 in docker-compose.yml. For example:
 * to increase the message.max.bytes parameter add KAFKA_MESSAGE_MAX_BYTES: 2000000 to the environment section.
 * to turn off automatic topic creation set KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'
 
2. To interact with Kafka container use the kafka-shell as:
```$ start-kafka-shell.sh <DOCKER_HOST_IP> <ZK_HOST:ZK_PORT>```
Example:
```$ ./start-kafka-shell.sh 192.168.86.89 192.168.86.89:2181```

3. Testing:
To test your setup, start a shell, create a topic and start a producer:

```
$ $KAFKA_HOME/bin/kafka-topics.sh --create --topic user_msg \
--partitions 4 --zookeeper $ZK --replication-factor 1
$ $KAFKA_HOME/bin/kafka-topics.sh --describe --topic user_msg --zookeeper $ZK 
$ $KAFKA_HOME/bin/kafka-console-producer.sh --topic=user_msg \
--broker-list=`broker-list.sh`
```

Start another shell and start a consumer:
```$ $KAFKA_HOME/bin/kafka-console-consumer.sh --topic=topic --zookeeper=$ZK```

Type in producer shell and it should be published to the client's shell.

4. To test with Java clients: 

mq-common/MessageProducer.java - writes to the user_msg topic (called from slackbot/MessageListener.java)
command-processor-service/ConsumerLoop.java - polls from user_msg topic 

