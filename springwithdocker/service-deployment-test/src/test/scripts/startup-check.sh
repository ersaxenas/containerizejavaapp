#!/bin/bash

containers="$1"

echo "Waiting for these containers to finish processing:"
for container in ${containers}; do
    echo "  ${container}"
done
echo ""

for container in ${containers}; do
    (
        for i in $(seq 1 24); do
            [ "$(docker inspect --format='{{.State.Running}}{{.State.ExitCode}}' ${container})" = "false0" ] && {
                echo "Docker container ${container} has completed successfully";
                exit 0;
            }
            sleep 5
        done
        exit 1
    ) || {
        printf "\n DOCKER CONTAINER ${container} FAILED TO START, LAST 250 LINES OF DOCKER LOGS FOLLOWS:\n\n";
        docker logs --tail=250 ${container};
        exit 1;
     }
done


