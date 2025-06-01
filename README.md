# Jedi Planner

## Overview
Jedi Planner is a personal task management tool designed to help users organize their tasks and projects efficiently. It provides a simple and intuitive interface for creating, managing, and tracking tasks, making it easier to stay on top of your workload.

## Features
- **Task Management**: Create, edit, and delete tasks with ease.
- **Project Organization**: Group tasks by tagging for better-organization.
- **Due Dates**: Set due dates for tasks to keep track of deadlines.
- **Prioritization**: Assign priority levels to tasks to focus on what matters most.
- **Search and Filter**: Quickly find tasks using search and filter options.
- **User Authentication**: Secure user authentication to protect your data.
- **Responsive Design**: Works seamlessly on both desktop and mobile devices.
- **Dark Mode**: Switch to dark mode for a more comfortable viewing experience in low-light conditions.

## Technologies Used
- **Spring Boot**: The backend framework for building the RESTful API.
- **Spring Data R2DBC**: For reactive database access.
- **GraphQL**: For flexible and efficient data querying.

## ERD Diagram
```mermaid
erDiagram
    users ||--o{ tasks : has
    users ||--o{ tags : has
    tasks ||--o{ task_tags : associated_with
    tags ||--o{ task_tags : associated_with
    
    users {
        UUID id PK "v7"
        string email UK "not null"
        string username "not null"
        string password "be hashed"
        timestamp created_at "default now()"
        timestamp updated_at "default now()"
    }

    tasks {
        UUID id PK "v7"
        UUID user_id FK "v7"
        string title "not null"
        string description "nullable"
        boolean is_completed "not null and default false"
        timestamp due_date "not null"
        timestamp created_at "not null and default now()"
        timestamp updated_at "not null and default now()"
    }
    
    task_tags {
        UUID task_id PK "v7"
        UUID label_id PK "v7"
    }

    tags {
        UUID id PK
        UUID user_id FK "v7"
        UUID task_id FK
        string name "not null"
        string color "not null, will be defined by services"
    }