# SkyControl Pro - Backend

Spring Boot REST API for drone fleet management.

## Setup

1. Configure database in `src/main/resources/application.properties`
2. Run `./mvnw clean install`
3. Run `./mvnw spring-boot:run`

## API Documentation

Access Swagger UI at: http://localhost:8080/swagger-ui.html

## Testing

Run tests: `./mvnw test`
Coverage report: `./mvnw test jacoco:report`
