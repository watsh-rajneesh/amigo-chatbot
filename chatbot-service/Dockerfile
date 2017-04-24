FROM sjsucohort6/oracle-java:8

MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

WORKDIR /chatbot-service

ADD ./target/chatbot-service-1.0-SNAPSHOT.jar /chatbot-service
ADD ./config.yml /chatbot-service

CMD ["java", "-jar", "chatbot-service-1.0-SNAPSHOT.jar", "server", "config.yml"]