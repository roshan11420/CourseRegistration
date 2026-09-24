# Online Student Course Registration System

This project is a Spring Boot application for managing students, courses, and enrollments. It includes a modern HTML/CSS/JavaScript frontend served from the Spring Boot static folder.

## Features

- Student management
- Course management
- Enrollment validation
- Prerequisite checks
- Capacity validation
- Dashboard metrics
- REST APIs with JSON responses

## Requirements

- Java 17+
- Maven

## Database

By default the project is configured to use an embedded H2 database for local development (no external DB required). To use Postgres, see the commented example below.

```properties
# H2 (default)
spring.datasource.url=jdbc:h2:mem:course_reg;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=

# PostgreSQL (uncomment to use Postgres)
# spring.datasource.url=jdbc:postgresql://localhost:5432/finguard
# spring.datasource.username=postgres
# spring.datasource.password=your_password
```

## Run the application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080/
```

## Project structure

```text
src/main/java/com/OnlineStudentCourse/CourseRegistration/
  config/
  controller/
  dto/
  exception/
  model/
  repository/
  service/
src/main/resources/static/
  index.html
  styles.css
  script.js
```

## Notes

- `ddl-auto=update` keeps the database schema in sync.
- The app seeds a few sample students and courses on startup when the database is empty.
