CREATE TABLE user_profiles
(
    id             UUID PRIMARY KEY,
    user_id        SERIAL       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    place_of_birth VARCHAR(255),
    date_of_birth  DATE,
    profile_pic    VARCHAR(255),
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT now()
);