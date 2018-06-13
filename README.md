# Twitter Stream

![SouJava](https://soujavablog.files.wordpress.com/2011/01/logo-soujava-top.jpg)


This project is an SouJava Team effort to get together many Automation test technologies like MapDB, Jackson, JUnit, Twitter Stream API, into something real, updated, tested and that is ready-made for use in a CI environment.

This project will get you going with your Automation testing in few minutes, you will be able to test the following technologies:


 * MapDB
 * Jackson (fasterxml) 2.x
 * JUnit
 * Twitter Stream API
 * Maven
 * Maven surefire, compiler, shade, exec plugin
 * Docker
 * Bash Scripts

This project exemplifies how to:

+ Connect to the [Twitter Streaming API](https://dev.twitter.com/streaming/overview)
+ Filter messages that track on "java".
+ Retrieve the incoming messages for a period of seconds or up to a XYZ amount of messages.
+ Application returns messages grouped by user (users sorted chronologically, ascending).
+ Messages are sorted chronologically, ascending.
+ All the twits found are saved into twitter.log.
+ App keeps track of messages retrieved statistics across multiple runs of the application.
+ The application can run as a Docker container.
+ The application does not need to get PIN authorization after first successful authentication.


# Linux instructions:

## Install:
`make install`

## Run
`make start`

## Build Docker
`make docker`

## Run Docker
`make docker-build`

## Clean
`make clean`

## Test
`make test`

## Clean databases
`make clean-db`



# Only commands:
## Install:
`mvn install`

## Run
`mvn exec:java`

## Build Docker
`sudo docker build -t biever-tweets .`

## Run Docker
`sudo docker run biever-tweets mvn exec:java`

## Clean
`mvn clean`

## Test Unit
`mvn test -P UnitTests`

## Test Integration
`mvn test -P IntegrationTests`

## Clean databases
```
rm -rf ./stats.db;
rm -rf ./config.db;
rm -rf twitter.log;
```


## Author

Ivens F. V. Signorini
[StackOverflow](https://stackoverflow.com/story/ivens-signorini)
[LinkedIn](https://www.linkedin.com/in/ivens-signorini)

Thomas Modeneis
[StackOverflow](https://careers.stackoverflow.com/thomasmodeneis)
[LinkedIn](https://uk.linkedin.com/in/thomasmodeneis)



License
=======

This module is licensed under the MIT license.