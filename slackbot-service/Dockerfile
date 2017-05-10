FROM sjsucohort6/oracle-java:8

MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

WORKDIR /slackbot

ADD ./target/slackbot-service-1.0-SNAPSHOT.jar /slackbot

CMD ["java", "-jar", "slackbot-service-1.0-SNAPSHOT.jar"]