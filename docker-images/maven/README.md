# Docker Oracle JDK 8 + Maven image

## Steps to build image:
```bash
docker login
docker build -t="sjsucohort6/jdk8-maven:3.3" .
docker tag sjsucohort6/jdk8-maven:3.3 sjsucohort6/jdk8-maven:3.3
docker push sjsucohort6/jdk8-maven:3.3
```

## Alternatively pull from docker hub
```bash
docker pull sjsucohort6/jdk8-maven:3.3
```

## Steps to use:
```bash
git clone https://github.com/sjsucohort6/amigo-chatbot.git
cd amigo-chatbot
docker run -it --rm -v "$PWD":/app -w /app sjsucohort6/jdk8-maven:3.3 mvn clean install -DskipTests
```

