#!/usr/bin/env bash

while ! ping -c1 $1; do
  sleep 5
done
