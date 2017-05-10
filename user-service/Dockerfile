FROM sjsucohort6/oracle-java:8

MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

WORKDIR /user-service

ADD ./target/user-service-1.0-SNAPSHOT.jar /user-service
ADD ./config_production.yml /user-service

CMD ["java", "-jar", "user-service-1.0-SNAPSHOT.jar", "server", "config_production.yml"]