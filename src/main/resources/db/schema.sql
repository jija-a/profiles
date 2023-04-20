CREATE TABLE users
(
    id                SERIAL PRIMARY KEY,
    first_name        VARCHAR(255)        NOT NULL,
    last_name         VARCHAR(255)        NOT NULL,
    email             VARCHAR(255) UNIQUE NOT NULL,
    password          VARCHAR(255)        NOT NULL,
    registration_date TIMESTAMP DEFAULT now()
);

CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE user_role
(
    user_id INTEGER NOT NULL REFERENCES users (id),
    role_id INTEGER NOT NULL REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE permissions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE role_permission
(
    role_id       INTEGER NOT NULL REFERENCES roles (id),
    permission_id INTEGER NOT NULL REFERENCES permissions (id),
    PRIMARY KEY (role_id, permission_id)
);
