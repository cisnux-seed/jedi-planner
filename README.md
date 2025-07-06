# Jedi Planner

## Overview

Jedi Planner is a personal task management tool designed to help users organize their tasks and projects efficiently. It
provides a simple and intuitive interface for creating, managing, and tracking tasks, making it easier to stay on top of
your workload.

## Features

- **Task Management**: Create, edit, and delete tasks with ease.
- **Project Organization**: Group tasks by tagging for better-organization.
- **Due Dates**: Set due dates for tasks to keep track of deadlines.
- **Search and Filter**: Quickly find tasks using search and filter options.
- **User Authentication**: Secure user authentication to protect your data.

## Technologies Used

- **Spring Boot**: The backend framework for building the RESTful API.
- **Spring Data R2DBC**: For reactive database access.
- **GraphQL**: For flexible and efficient data querying.

## ERD Diagram

```mermaid
erDiagram
    users ||--o{ tasks : has
    users ||--o| user_profiles : has
    users ||--o{ authentications : has
    
    users {
        long id PK "v7"
        varchar(255) email UK "not null"
        varchar(255) username UK "not null"
        varchar(255) password "be hashed"
        timestamp created_at "default now()"
        timestamp updated_at "default now()"
    }
    
    user_profiles {
        UUID id PK "v7"
        long user_id FK "v7"
        varchar(255) first_name "not null"
        varchar(255) last_name "not null"
        varchar(255) profile_pic "nullable"
        varchar(255) place_of_birth "nullable"
        date date_of_birth "nullable"
        timestamp created_at "default now()"
        timestamp updated_at "default now()"
    }
    
    authentications {
       token varchar(512) PK
       long user_id FK
       timestamp created_at "default now()"
   }
    
    tasks {
        UUID id PK "v7"
        long user_id FK "v7"
        varchar(255) title "not null"
        text description "nullable"
        boolean is_completed "not null and default false"
        timestamp due_date "not null"
        timestamp created_at "not null and default now()"
        timestamp updated_at "not null and default now()"
    }