[![Build Status](https://travis-ci.org/mdomasevicius/itunes-top5.svg?branch=master)](https://travis-ci.org/mdomasevicius/itunes-top5)

# iTunes artists & top 5 albums

App that can search for artists by term via iTunes API, add artists to favourites and view artist's top5 albums.

## Prerequisites

- JAVA 11
- docker 19.03.5
- docker-compose 1.25.4

## Quickstart

`$ .gradlew clean build` - builds application and runs tests (tests will spin up docker containers of DB and app on ports 7001 and 5401 respectively)  

`$ docker-compose up` - will start DB and app

`$ docker-compose up dev` - will start app dependencies (in this case only DB) so you can run the application from your IDE of choice if you wish. Executing tests from IDE requires a running application

Swagger documentation can be reached via: [http://localhost:7001/swagger-ui.html](http://localhost:7001/swagger-ui.html)

You can also checkout travis build.

## Library choices and reasoning

- `spring-boot-starter-web` as per recommendation and because job application emphasizes `spring`.
- `liquibase` for DB migration. Perhaps this is overkill however it was the least time consuming and is favoured by most JAVA developers.  
- `okhttp3` as http client. I am aware of JAVA 11 http client and was excited to go with it until I ran into bugs. Lacking experience in dealing with it I switched to `okhttp3` as I was more familiar with it.
- `jOOQ` as a query mapping layer. It's more practical than `java.sql.Connection` API and less magical than `Hibernate` plus it's free with open source DB projects.
- `swagger` some documentation just to cover more basis.

- `groovy` & `spock` for testing. Personally I like `groovy` map shorthand literals that removes much noise from constructing JSON requests, also groovy removes a lot of noise and loose typing makes tests cleaners. And spock gives more specification look n feel to tests (specs/features). 

## Component testing

This method of testing has 2 major benefits:
1) It has faster feedback loop so you are more likely to go TDD
2) It is mostly meant to test contracts of APIs. This means that you can move things around in implementation quicker then you would in example if you were to use pure JUnit approach.

## Acknowledgement

- I chose not to obsess over every aspect of optimizing querying iTunes API, although I have more ideas on how to improve its usage.
- Tests might become unstable during period of an hour since you can hit rate limit
- No fall-back implemented for when iTunes API rate limit is reached
- Public fields over setters/getters. Many java developers find this uncanny but when you look into it every major framework and library supports this approach and since java is on its own a very verbose language shaving every bit of noise in code helps maintainability down the line. 