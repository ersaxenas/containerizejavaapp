FROM openjdk:8-jdk

LABEL maintainer="Saurabh Saxena"
ARG DB_VER=1.4.199
ENV DB_HOME=/opt/h2db

EXPOSE 81 1521

RUN apt-get update && apt-get install bash && apt-get install -y curl && mkdir ${DB_HOME} && mkdir ${DB_HOME}/bin && mkdir ${DB_HOME}/data \
&& apt-get install -y dos2unix

ADD target/h2-${DB_VER}.jar ${DB_HOME}/bin/h2-${DB_VER}.jar
COPY src/main/docker/run-h2db.sh /usr/local/bin/run-h2db.sh

ENTRYPOINT ["run-h2db.sh"]