# 🎓 Course Registration System

A full-stack **Course Registration System** built with **Java, Spring Boot, PostgreSQL, HTML, CSS, and JavaScript**. The system allows students to register and enroll in courses, while administrators can manage courses and view enrolled students.

## 🚀 Features

### 👨‍🎓 Student

* Student registration
* Student login/role-based UI
* View available courses
* Enroll in courses
* View enrollment information

### 👨‍💼 Admin

* Add new courses
* Update existing courses
* Delete courses
* View available courses
* View students enrolled in courses

### ⚙️ Backend

* REST APIs using Spring Boot
* JPA/Hibernate for database interaction
* PostgreSQL for persistent data storage
* DTO-based API responses
* JOIN-FETCH queries for retrieving enrollment-related data
* Service and repository layer architecture

### 🖥️ Frontend

* HTML
* CSS
* JavaScript
* Role-aware student and admin panels
* API integration using JavaScript

---

## 🛠️ Tech Stack

| Technology      | Usage                            |
| --------------- | -------------------------------- |
| Java            | Backend development              |
| Spring Boot     | REST API & application framework |
| Spring Data JPA | Database access                  |
| Hibernate       | ORM                              |
| PostgreSQL      | Database                         |
| HTML            | Frontend structure               |
| CSS             | Frontend styling                 |
| JavaScript      | Frontend logic & API integration |
| Maven           | Build & dependency management    |
| Git & GitHub    | Version control                  |

---

## 📁 Project Structure

```text
CourseRegistration/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ...
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── script.js
│   │       │   └── style.css
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

---

## 🗄️ Database Design

The application uses **PostgreSQL** for persistent data storage.

Main entities:

```text
AppUser
   │
   └── Student
          │
          └── Enrollment
                   │
                   └── Course
```

### Main Entities

**AppUser**

* User information
* Username
* Role

**Student**

* Student information
* Username association

**Course**

* Course details
* Course name
* Description
* Other course-related information

**Enrollment**

* Student-course relationship
* Enrollment information

---

## 🔄 Application Flow

### Student Flow

```text
Register
   ↓
Student Account
   ↓
View Courses
   ↓
Select Course
   ↓
Enroll
   ↓
Enrollment Stored in PostgreSQL
```

### Admin Flow

```text
Admin Panel
   ↓
Manage Courses
   ├── Add Course
   ├── Update Course
   └── Delete Course
   ↓
View Enrollments
   ↓
View Enrolled Students
```

---

## 🔌 API Functionality

The backend exposes REST APIs for operations such as:

```text
POST   /api/register
POST   /api/enroll
GET    /api/courses
POST   /api/courses
PUT    /api/courses/{id}
DELETE /api/courses/{id}
GET    /api/enrollments
```

> API paths may vary depending on the controller implementation in the project.

---

## ⚙️ Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR-USERNAME/CourseRegistration.git
```

```bash
cd CourseRegistration
```

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE course_registration;
```

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/course_registration
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Build the Project

Using Maven:

```bash
mvn clean package
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the generated JAR:

```bash
java -jar target/<your-jar-name>.jar
```

### 5. Open in Browser

```text
http://localhost:8080
```

---

## 📸 Screenshots

Add screenshots of your application here.

### Student Dashboard

```text
Add screenshot here
```

### Admin Dashboard

```text
Add screenshot here
```

### Course Management

```text
Add screenshot here
```

### Enrollment View

```text
Add screenshot here
```

---

## 🧠 Key Implementation Highlights

* Designed a layered **Spring Boot architecture** using Controller, Service, Repository, and Entity layers.
* Implemented `AppUser`, `Student`, and `Enrollment` domain models.
* Created an `EnrollmentResponse` DTO for structured enrollment responses.
* Used **JOIN-FETCH queries** to retrieve related enrollment data efficiently.
* Linked students with usernames through the service layer.
* Added separate student and admin frontend panels.
* Configured **PostgreSQL** for persistent application data.
* Packaged the application successfully using **Maven**.

---

## 🔐 Security Note

The current version uses client-side role handling and does **not yet implement complete server-side authorization**.

For a production deployment, the application should be enhanced with:

* Spring Security authentication
* Server-side role-based authorization
* Password hashing
* JWT/session-based authentication
* Input validation
* CSRF protection where applicable
* Secure environment-based database credentials

---

## 🔮 Future Improvements

* [ ] Implement complete Spring Security authentication
* [ ] Add JWT-based authorization
* [ ] Add password encryption using BCrypt
* [ ] Add course search and filtering
* [ ] Prevent duplicate enrollments
* [ ] Add student profile management
* [ ] Add pagination for courses and enrollments
* [ ] Add email notifications
* [ ] Add Docker support
* [ ] Deploy backend and PostgreSQL database to the cloud
* [ ] Add automated testing

---

## 📚 What I Learned

Through this project, I gained practical experience with:

* Spring Boot REST API development
* Spring Data JPA and Hibernate
* PostgreSQL database integration
* Entity relationships
* DTO design
* JOIN-FETCH queries
* Frontend-backend API integration
* Maven build and packaging
* Git and GitHub project management

---

## 👨‍💻 Author

**Roshan Sahu**

B.Tech — Computer Science and Engineering

### Connect With Me

* GitHub: `https://github.com/roshan11420`
* LinkedIn: `Add your LinkedIn profile here`

---

## ⭐ Support

If you found this project useful, consider giving the repository a **⭐ star**!
