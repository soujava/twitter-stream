#!/usr/bin/env bash

default: start

clean:
	echo "============== Clean app";
	mvn clean;

clean-db:
	rm -rf ./stats.db;
	rm -rf ./config.db;
	rm -rf twitter.log;

log:
	echo "============== Tail logs for app";
	tail -f ./twitter.log;

start:
	echo "============== Compiling app";
	mvn compile;
	echo "============== Starting app";
	mvn exec:java;

docker-build:
	echo "============== Building Docker";
	sudo docker build -t biever-tweets .

docker-run:
	echo "============== Building Docker";
	sudo docker run biever-tweets mvn exec:java

test: clean
	mvn compile;
	echo "============== Testing app";
	mvn test;

test-integration: clean
	mvn compile;
	echo "============== Testing app - IT Tests";
	mvn test -P IntegrationTests;

test-unit: clean
	mvn compile;
	echo "============== Testing app - Unit Tests";
	mvn test -P UnitTests;

.PHONY: start, clean, test, log
