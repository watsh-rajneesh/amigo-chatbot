FROM sjsucohort6/oracle-java:8

MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

WORKDIR /riabot-service

ADD ./target/riabot-service-1.0-SNAPSHOT.jar /riabot-service
ADD ./config.yml /riabot-service

CMD ["java", "-jar", "riabot-service-1.0-SNAPSHOT.jar", "server", "config.yml"]