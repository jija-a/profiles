INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO permissions (name)
VALUES ('CREATE'),
       ('READ'),
       ('UPDATE'),
       ('DELETE');

INSERT INTO users (first_name, last_name, email, password, registration_date)
VALUES ('John', 'Doe', 'johndoe@example.com', 'password123', DEFAULT),
       ('Jane', 'Doe', 'janedoe@example.com', 'password456', DEFAULT),
       ('Bob', 'Smith', 'bobsmith@example.com', 'password789', DEFAULT);

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (3, 1);

INSERT INTO role_permission (role_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (2, 2),
       (2, 3);
