FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk-headless \
                       gradle && \
    apt-get clean
RUN update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

WORKDIR /irma_keyshare_server

COPY src /irma_keyshare_server/src
COPY gradle /irma_keyshare_server/gradle
COPY gradlew /irma_keyshare_server
COPY gradlew.bat /irma_keyshare_server
COPY build.gradle /irma_keyshare_server

CMD ["./gradlew", "appRun", "--no-daemon",  "--no-parallel"]
