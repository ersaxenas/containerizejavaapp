FROM openjdk:8-jdk

LABEL maintainer="Saurabh Saxena"

ARG APP_VER=1.0
ENV APP_HOME=/opt/dataloader

RUN apt-get update && apt-get install bash && apt-get install -y curl && mkdir ${APP_HOME} && apt-get install -y dos2unix
ADD target/h2db-data-load.jar ${APP_HOME}/h2db-data-load.jar
COPY src/main/docker/load_data_to_h2.sh /usr/local/bin/load_data_to_h2.sh
RUN dos2unix -q /usr/local/bin/load_data_to_h2.sh && chmod a+x /usr/local/bin/load_data_to_h2.sh

ENTRYPOINT ["load_data_to_h2.sh"]



