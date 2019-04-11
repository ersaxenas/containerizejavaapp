#!/usr/bin/env bash
set -e

DATE_WITH_TIME=`date "+%Y%m%d-%H%M%S"`
echo "[${DATE_WITH_TIME}] : removing existing containers"

container_name_prefix=$1
[ -z "$container_name_prefix" ] && { echo "Container name prefix must be provide. Exiting the process."; exit 1; }

list_of_containers="$(docker ps -a -q --filter=name=${container_name_prefix})"

if [ -z "${list_of_containers}" ]; then
   echo "No containers found with prefix: ${container_name_prefix}"
else
    docker rm -f -v ${list_of_containers}
    echo "Successfuly removed containers with prefix ${container_name_prefix}"
fi
