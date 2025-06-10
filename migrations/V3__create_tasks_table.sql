CREATE TABLE tasks
(
    id           UUID PRIMARY KEY,
    user_id      SERIAL       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    is_completed BOOLEAN      NOT NULL DEFAULT FALSE,
    due_date     TIMESTAMP    NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_tasks_user_id ON tasks (user_id);