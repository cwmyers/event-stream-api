#!/bin/sh

DOCKER_IMAGE=hypriot/rpi-java:latest DOCKER_PACKAGE_NAME=event-stream-api/rpi-event-stream-api ./sbt docker:publish
