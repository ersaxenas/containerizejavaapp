#!/usr/bin/env bash
java -Xmx256m -Xms256m -cp ${DB_HOME}/bin/*.jar org.h2.tools.Server \
 	-web -webAllowOthers -webPort 81 \
 	-tcp -tcpAllowOthers -tcpPort 1521 \
 	-baseDir ${DB_HOME}/data \
 	-webAdminPassword admin -ifNotExists

