ALTER TABLE user_profiles
    ADD CONSTRAINT user_profiles_user_id_unique UNIQUE (user_id);