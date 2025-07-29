## MyTaskManager – Project & Task Management REST API

A backend REST API built with **Java Spring Boot** to manage projects and their associated tasks.  
The application uses a layered architecture, JPA entity relationships, and is version-controlled via GitHub to simulate real-world team collaboration.

## Technologies Used

- Java 17
- Spring Boot (Web, Data JPA)
- Lombok (annotations to reduce boilerplate)
- MySQL (via port 3307) / H2 (optional)
- Maven
- Git + GitHub (for versioning and project tracking)
- RESTful API
- Cross-Origin Resource Sharing (CORS) enabled


##  Main Features

-  **Projects**
  - Create, view, update, and delete
  - Includes name, description, start & end date
-  **Tasks**
  - Assigned to projects (Many-to-One)
  - Includes title, description, status, due date
-  **Status enum**
  - `TODO`, `IN_PROGRESS`, `DONE`, `FAILED`, `DELAYED`
-  **Git-based development workflow**
  - Feature branches for each component
  - Descriptive commit messages
  - Project history managed via GitHub


## API Overview

###  Project Endpoints

| Method | Endpoint             | Description             |
|--------|----------------------|-------------------------|
| GET    | `/api/projects`      | Get all projects        |
| GET    | `/api/projects/{id}` | Get project by ID       |
| POST   | `/api/projects`      | Create new project      |
| PUT    | `/api/projects/{id}` | Update project by ID    |
| DELETE | `/api/projects/{id}` | Delete project by ID    |

###  Task Endpoints

| Method | Endpoint                    | Description                    |
|--------|-----------------------------|--------------------------------|
| GET    | `/api/tasks`                | Get all tasks                  |
| GET    | `/api/tasks/project/{id}`   | Get all tasks for a project    |
| POST   | `/api/tasks`                | Create new task                |
| PUT    | `/api/tasks/{id}`           | Update task by ID              |
| DELETE | `/api/tasks/{id}`           | Delete task by ID              |



##  Project Structure

src/
└── main/
├── java/com/portfolio/mytaskmanager/
│ ├── controller/
│ │ ├── ProjectController.java
│ │ └── TaskController.java
│ ├── dto/
│ │ ├── ProjectDTO.java
│ │ └── TaskDTO.java
│ ├── entity/
│ │ ├── Project.java
│ │ ├── Task.java
│ │ └── Status.java
│ ├── repository/
│ │ ├── ProjectRepo.java
│ │ └── TaskRepo.java
│ ├── service/
│ │ ├── ProjectService.java
│ │ └── TaskService.java
│ └── MytaskmanagerApplication.java
└── resources/
├── application.properties


## Entity Relationships

Project => has many Task entities (@OneToMany)
Task => belongs to one Project (@ManyToOne)
Tasks are fetched by project using: List<Task> findByProject(Project project);


## Learning Objectives

- Designing a RESTful API with layered architecture
- Managing entity relationships (1:N, N:1)
- Using DTOs and service abstraction
- Versioning with Git: working in feature branches and committing changes
- Using Spring Boot with JPA and Lombok
- Backend/frontend integration via CORS

## AUTHOR ##

Created by Matúš Bučko as a full-stack Java development portfolio project.
The goal was to simulate a real-world backend team workflow with proper separation of concerns, data flow, and Git-based collaboration.

>> Frontend repository (REACT) << 
https://github.com/Matus14/https---github.com-Matus14-work-mytaskmanager_frontend


