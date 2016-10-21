#!/bin/sh

DOCKER_IMAGE=hypriot/rpi-java:latest DOCKER_PACKAGE_NAME=rpi-event-stream-api ./sbt docker:publish