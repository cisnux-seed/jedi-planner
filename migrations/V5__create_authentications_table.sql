CREATE TABLE authentications
(
    token VARCHAR2(512) PRIMARY KEY,
    email VARCHAR2(50) NOT NULL REFERENCES users (email),
    created_at TIMESTAMP DEFAULT SYSDATE NOT NULL
);