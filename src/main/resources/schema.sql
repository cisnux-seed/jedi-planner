CREATE TABLE IF NOT EXISTS tasks
(
    id            VARCHAR(255) PRIMARY KEY,
    title         VARCHAR(255)             NOT NULL,
    description   VARCHAR(255),
    has_completed BOOLEAN                  NOT NULL DEFAULT FALSE,
    due_date      TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL
);