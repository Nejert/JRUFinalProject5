# JRU, Led Zeppelin, Project Module 5
## Task Management System (TMS)

**Task Management System** is a RESTful application built with Spring Boot, designed for managing personal tasks. This training project covers the full development lifecycle: from database design and business logic implementation to security configuration, monitoring, and automated deployment.

---
## 🚀 Key Features

### User Features:

- **Authentication & Authorization**: Secure registration and login using JWT (JSON Web Tokens).
- **Task Management (CRUD)**: Create, read(view), update(edit), and delete tasks and users.
### Technical Features:

- **Centralized Logging**: ELK Stack integration for system-wide log analysis.
- **Monitoring**: Real-time application health tracking via Spring Boot Actuator.
- **Containerization**: Full Docker and Docker Compose support for instant deployment.

---
## 🛠 Tech Stack

| **Layer**               | **Technologies**                                                                   |
| ----------------------- |------------------------------------------------------------------------------------|
| **Runtime & Framework** | **Java 21**, **Spring Boot 4**                                                     |
| **Database**            | **PostgreSQL** (Production), **Spring Data JPA** (Hibernate)                       |
| **Security**            | **Spring Security**, **JJWT** (Java JSON Web Token)                                |
| **API Documentation**   | **Springdoc OpenAPI (Swagger UI)**                                                 |
| **Utilities**           | **Lombok**, **MapStruct**, **Bean Validation**                                     |
| **Monitoring & Logs**   | **Prometheus**, **Grafana**, **ELK Stack** (Elasticsearch, Logstash, Kibana)       |
| **Testing**             | **JUnit 6**, **Mockito**, **Testcontainers** (integration testing with PostgreSQL) |

---
## 📐 Project Architecture

The application is built using a classic multi-layered architecture:
1. **Controller Layer**: Processing HTTP requests and validating incoming data.
2. **Service Layer**: Implementing business logic and managing transactions (`@Transactional`).
3. **Repository Layer**: Interacting with the database via Spring Data JPA.
4. **Security Layer**: Filtering requests and checking access rights.

```plaintext
[Client] <===> [REST API] <===> [Service Layer] <===> [Database Module]
                                      |
                                [Security Module]
                                      |
                            [Monitoring & Logging Module]
```

---
## 📊 Data Model

Key system entities:
- **User**: `id, username, email, password, role`
- **Task**: `id, title, description, deadline, status, user_id (FK)`
  Relationship: One user can own multiple tasks. (One-to-Many).

---
## 🔌 Endpoints API Description
All requests are sent to the base URL.: `http://localhost:8080/api/v1`.
### 👥 User Management (`user-controller`)
Allows an administrator to manage accounts or a user to retrieve profile information.

| **Method** | **Endpoint** | **Description** | **Note** |
| ----------- | ------------- | ------------------------------------------- | ---------------------------------------------------------------- |
| **GET** | `/users` | Get a list of all users. | Returns an array of `User` objects. |
| **POST** | `/users` | Create a new user (admin panel). | Requires `login`, `password`, `role`. |
| **GET** | `/users/{id}` | Get user data by ID. | Returns a `User` by ID. |
| **PUT** | `/users/{id}` | Full user data update. | Replaces all object fields. |
| **PATCH** | `/users/{id}` | Partial data update. | You can update only individual fields (for example, only `email`). |
| **DELETE** | `/users/{id}` | Delete a user by ID. | Deletes a `User` by ID. |

---
### 📝 Task Management (`task-controller`)
The main functionality of the system for working with to-do lists.

| **Method** | **Endpoint** | **Description** | **Note** |
| ----------- | ------------- | ----------------------------------------- | ---------------------------------------------- |
| **GET** | `/tasks` | List all available tasks. | Returns an array of `Task` objects. |
| **POST** | `/tasks` | Create a new task. | Requires a `title` (min. 3 characters), `description`. |
| **GET** | `/tasks/{id}` | Detailed information about a specific task. | Returns a `Task` by id. |
| **PUT** | `/tasks/{id}` | Fully edit a task. | Requires all required fields. |
| **PATCH** | `/tasks/{id}` | Change the task status or title. | Useful for changing the `status` or deadline. |
| **DELETE** | `/tasks/{id}` | Delete a task from the system. | Deletes a `Task` by id. |

---
### 🔐 Authentication and Registration (`auth-controller`)

| **Method** | **Endpoint**     | **Description**         | **Request Body / Parameters**            |
| ---------- | ---------------- | ----------------------- | ---------------------------------------- |
| **POST**   | `/auth/register` | Register a new user.    | `login`, `password`, `email` (optional). |
| **POST**   | `/auth/login`    | Log in and get a token. | `login`, `password`.                     |
| **POST** | `/auth/monitoring` | Service endpoint for Prometheus. | Query params: `login`, `password`. |

---
## 🏗 Data Models (Schemas)

To work correctly with the API, use the following data structures:
### User
- **Login**: from 3 to 50 characters.
- **Roles**: `ADMIN`, `USER`.
- **Email**: must match the email format.
```json
POST /api/v1/users
{
    "login" : "login",
    "password" : "password",
    "email" : "example@email.com",
    "role" : "ADMIN"
}
```
### Task
- **Title (`title`)**: 3 to 100 characters.
- **Deadline (`deadline`)**: date and time format (ISO 8601).
- **Status (`status`)**: a string describing the current status of the task.
```json
POST /api/v1/tasks
{
    "title": "Complete Project",
    "description": "Complete Documentation",
    "deadline": "2026-01-01T00:00:00",
    "status": "IN_PROGRESS",
    "userId": 1
}
```
---
## 📊 Monitoring and Logging

The project has a comprehensive application health monitoring system deployed via Docker Compose.
### 1. Metrics and Visualization (Prometheus & Grafana)

To monitor JVM performance, HTTP requests, and system health, the following are used:
- Spring Boot Actuator: Collects internal application metrics.
    - `GET /actuator/prometheus` endpoint where Prometheus collects data.
    - Available at: http://localhost:9090.
- Grafana: A platform for visualization and dashboard creation.
    - Available at: http://localhost:3000.
### 2. Centralized Logging (ELK Stack)

The Elasticsearch, Logstash, and Kibana stack is used to analyze system operation and troubleshoot errors:
- **Logstash**: Aggregates application logs and transfers them to storage.
- **Elasticsearch**: Scalable log search and storage system.
    - Available at: http://localhost:9200.
- **Kibana**: Interface for convenient log searching and filtering.
    - Available at: http://localhost:5601.

⚙️ Logstash, Prometheus, and Grafana configurations are stored in the `.cfg/` folder.
### 🛠 Health Check

To quickly check the service's health, use:
- `GET http://localhost:8080/actuator/health` — returns an `UP` status if the application and database are working correctly.
---
## 📖Interactive API Documentation (Swagger UI)

Springdoc OpenAPI is integrated into the project, which automatically generates documentation based on the application's controllers and models.
### How to use:

1. Run the application (via Docker Compose or an IDE).
2. Go to: [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html).
### Swagger UI Features:

- **Endpoint Visualization**: A complete list of all available URLs, grouped by controller (`Auth, User, Task`).
- **On-the-fly testing**: You can send real requests (GET, POST, PUT, PATCH, DELETE) directly from the browser and see server responses.
- **Schema Viewer**: Detailed descriptions of data structures, including required fields, data types, and validation constraints (e.g., `login` length or `email` format).
- **Authorization**: Support for testing secure endpoints by adding a JWT token via the "Authorize" button.

>**Note:** To work with the `User` and `Task` methods, you must first log in via `/api/v1/auth/login` or register via `/api/v1/auth/register`, obtain a token, and paste it into the Swagger authorization field (format `Bearer <your_token>`).

---
## 📦 Installation & Setup

### Prerequisites:

- **JDK 21**
- **Docker & Docker Compose**
- **Required free ports in Docker:** 3000, 5044, 5432, 5601, 8080, 9090, 9200.
### Steps:

1. **Clone the repository:**
    ```bash
    git clone https://github.com/Nejert/JRUFinalProject5.git
    cd JRUFinalProject5
    ```    
2. **Launch with Docker Compose:**
    ```bash
    docker-compose up -d
    ```
The API will be available at http://localhost:8080.