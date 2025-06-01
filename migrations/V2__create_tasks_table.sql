CREATE TABLE tasks
(
    id           RAW(16) PRIMARY KEY,
    email     VARCHAR2(50)              NOT NULL REFERENCES users (email),
    title        VARCHAR2(255)             NOT NULL,
    description  CLOB,
    is_completed NUMBER(1) DEFAULT 0       NOT NULL,
    due_date     TIMESTAMP                 NOT NULL,
    created_at   TIMESTAMP DEFAULT SYSDATE NOT NULL,
    updated_at   TIMESTAMP DEFAULT SYSDATE NOT NULL
);