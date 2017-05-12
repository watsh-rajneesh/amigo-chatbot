FROM sjsucohort6/oracle-java:8

MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

WORKDIR /commandprocessor

ADD ./target/command-processor-service-1.0-SNAPSHOT.jar /commandprocessor
ADD ./config.yml /commandprocessor
ADD ./aws_intent_command_mappings.json /commandprocessor

CMD ["java", "-jar", "command-processor-service-1.0-SNAPSHOT.jar", "server", "config.yml"]