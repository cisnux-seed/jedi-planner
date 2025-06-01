CREATE TABLE users
(
    email      VARCHAR2(50)              NOT NULL PRIMARY KEY,
    password   VARCHAR2(255)             NOT NULL,
    created_at TIMESTAMP DEFAULT SYSDATE NOT NULL,
    updated_at TIMESTAMP DEFAULT SYSDATE NOT NULL
);