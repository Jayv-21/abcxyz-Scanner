#!/bin/bash

cd ../src/
make
cd ../env
docker build --force-rm -t scanner .
xhost +
docker rmi -f $(docker images -a | grep "^<none>" | awk "{print $3}")
docker run -iv /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY=$DISPLAY scanner
