CREATE TABLE app_user (
    id           UUID PRIMARY KEY,
    firebase_uid VARCHAR(128)  NOT NULL UNIQUE,
    email        VARCHAR(255)  NOT NULL,
    display_name VARCHAR(255),
    role         VARCHAR(20)   NOT NULL DEFAULT 'CUSTOMER',
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_app_user_firebase_uid ON app_user (firebase_uid);
