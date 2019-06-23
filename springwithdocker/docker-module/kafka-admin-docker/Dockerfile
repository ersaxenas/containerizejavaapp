FROM openjdk:8-jdk

LABEL maintainer="Saurabh Saxena"

ARG APP_VER=1.0
ENV APP_HOME=/opt/kafka-topic-admin

RUN apt-get update && apt-get install bash && apt-get install -y curl && mkdir ${APP_HOME} && apt-get install -y dos2unix
ADD target/kafka-topic-admin.jar ${APP_HOME}/kafka-topic-admin.jar
COPY src/main/docker/run_kafka_admin.sh /usr/local/bin/run_kafka_admin.sh
RUN dos2unix -q /usr/local/bin/run_kafka_admin.sh && chmod a+x /usr/local/bin/run_kafka_admin.sh

ENTRYPOINT ["run_kafka_admin.sh"]



