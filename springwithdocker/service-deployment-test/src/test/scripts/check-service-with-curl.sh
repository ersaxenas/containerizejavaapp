#!/usr/bin/env bash

for i in {1..20};
do
curl -fsS $1 > /dev/null && echo 'success' && exit 0
sleep 5
done

echo 'Health check failed - Service is not available : '$1