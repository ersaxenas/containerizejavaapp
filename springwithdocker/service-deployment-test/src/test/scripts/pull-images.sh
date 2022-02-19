#!/bin/bash
set -e

for f in `ls docker-compose*.yml`; do
    docker-compose -f ${f} pull --ignore-pull-failures
done
