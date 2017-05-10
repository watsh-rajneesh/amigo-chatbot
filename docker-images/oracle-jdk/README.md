Oracle JDK 8 Docker Image
=========================
Based on [this] (https://runnable.com/docker/java/dockerize-your-java-application) article.

Oracle JDK 8 image can be used to build docker images for java applications

It can even be used to run java applications on hosts with no java but with docker installed.

For example, the following command will run the command: java -version.

```docker run --rm -it sjsucohort6/oracle-java:8 java -version```

