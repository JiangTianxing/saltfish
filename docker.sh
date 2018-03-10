#!/usr/bin/env bash

docker-compose -f docker-compose.yml stop $1
docker-compose -f docker-compose.yml rm -f $1

docker rmi -f saltfish/$1:latest

cd ./$1
mvn clean
mvn package docker:build -Dmaven.test.skip=true
