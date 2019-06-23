FROM openjdk:8-jdk

LABEL maintainer="Saurabh Saxena"

ARG APP_VER=1.0
ENV APP_HOME=/opt/restserver
EXPOSE 8080

RUN apt-get update && apt-get install bash && apt-get install -y curl && mkdir ${APP_HOME} && apt-get install -y dos2unix
ADD target/restapp-1.0-SNAPSHOT.jar ${APP_HOME}/restapp.jar
COPY src/main/docker/run-application.sh /usr/local/bin/run-application.sh
RUN dos2unix -q /usr/local/bin/run-application.sh && chmod a+x /usr/local/bin/run-application.sh

ENTRYPOINT ["run-application.sh"]



