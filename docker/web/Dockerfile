FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk-headless \
                       gradle && \
    apt-get clean
RUN update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

WORKDIR /app

COPY src /app/src
COPY build.gradle /app

RUN gradle wrapper
CMD ["./gradlew", "appRun", "--no-daemon",  "--no-parallel"]
