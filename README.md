# Feedback Microservice
This project is show case how to implement a very simple microservice which is created by using
Spring Boot. An application is storing feedback messages and make them accessible through REST API.

API operations:

* `POST /feedback` - store feedback
* `GET /feedback` - list of all feedback in system.
  * `GET /feedback?name={name}` - filter all feedback by author's name
* `GET /feedback/{id}` - find feedback by Id

More information on how to communicate with the REST interface is contained in Swagger Documentation on URL <http://localhost:8080/v2/api-docs>.

> You can use the Swagger UI to visualize and interact with the API. To activate this feature,
> you need to use maven profile `swagger-ui` during [building](#building) of project. This profile integrates
> Swagger UI into the application and exposes it on URL <http://localhost:8080/swagger-ui.html>.

All data are stored only in-memory storage [Spring Data Key Value](https://github.com/spring-projects/spring-data-keyvalue).  

Because this is a microservice I added [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)
to be production ready. This starter adds endpoints to monitor and interact with the application.
For example, you can use <http://localhost:8081/health> endpoint to check if microservice is ready.

### Building Docker image

In order to build docker image, you must have access to a Docker daemon.
The easiest way is to setup the environment variable `DOCKER_HOST`.
Alternatively, you can use the Maven property `docker.host` to point to the Docker daemon.

The Docker image can be created by using following goals:

```bash
mvn clean install -Pdocker
```

## Running the tests
JUnit tests can be executed by a following goal:

```
mvn test
```

## Running project
Simplest way how to run application locally is to use Maven goal
```bash
mvn spring-boot:run
```