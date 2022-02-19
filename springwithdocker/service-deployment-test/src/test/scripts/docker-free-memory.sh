#!/usr/bin/env sh
set -e

# this script works with the 32 bit shell in busybox that is part of Alpine as well as Ubuntu

if [ ! "$(ls -A /sys/fs/cgroup)" ]; then
    (
        echo "Error: /sys/fs/cgroup is empty"
        echo "Make sure you have add this as a volume before proceeding. Your docker run"
        echo "command should have the following option:"
        echo "  -v /sys/fs/cgroup:/sys/fs/cgroup:ro"
    ) 1>&2
    exit 1
fi

cgroup_mem_path=$(grep memory /proc/self/cgroup | cut -f3- -d:)
free_memory_bytes=$(cat /sys/fs/cgroup/memory${cgroup_mem_path}/memory.limit_in_bytes)
free_memory_megs=$(echo ${free_memory_bytes} | sed 's/......$//')

# Make sure that the "-m" docker option was specified. If it is not specified, then the
# free memory from cgget will be some enormous number that is much bigger than the system
# memory. Since we want ALL containers to be launched with some -m setting, we will
# simply enforce that -m must be secified...
system_memory=$(free -m | grep Mem: | awk '{print $2}')

if [ "${free_memory_megs}" -gt "${system_memory}" ]; then
    (
        echo "Error: cgroup memory is larger than available system memory"
        echo "  cgroup memory = ${free_memory_bytes}"
        echo "  system memory = ${system_memory}"
        echo "This probably means that you did not specify the -m docker run"
        echo "option. Add a -m setting and try again, e.g.:"
        echo "  docker run -m 1024m ..."
    ) 1>&2
    exit 1
fi

# Print the free memory and exit:
echo "${free_memory_bytes}"
