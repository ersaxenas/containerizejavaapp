FROM openjdk:8-jdk

LABEL maintainer="Saurabh Saxena"

ARG APP_VER=1.0
ENV APP_HOME=/opt/kafka-data-load

RUN apt-get update && apt-get install bash && apt-get install -y curl && mkdir ${APP_HOME} && apt-get install -y dos2unix
ADD target/kafka-data-loader.jar ${APP_HOME}/kafka-data-loader.jar
COPY src/main/docker/run_kafka_data_loader.sh /usr/local/bin/run_kafka_data_loader.sh
RUN dos2unix -q /usr/local/bin/run_kafka_data_loader.sh && chmod a+x /usr/local/bin/run_kafka_data_loader.sh

ENTRYPOINT ["run_kafka_data_loader.sh"]



