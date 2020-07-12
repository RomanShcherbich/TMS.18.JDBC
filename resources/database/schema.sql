/* Clearing DB tables */

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_to_role;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user_info;
DROP TABLE IF EXISTS user_to_role;

/*
  Creates table preserved user credentials
    id - identifier
    username - username
    password - password
    roles - string enumerated user roles by commas
 */
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(63)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles    VARCHAR(255) NOT NULL
);

CREATE TABLE user_role
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50),
    description VARCHAR(500),
    category    VARCHAR(200)
);

CREATE TABLE user_info
(
    id                  SERIAL PRIMARY KEY,
    login               VARCHAR(200) NOT NULL,
    first_name          VARCHAR(200),
    last_name           VARCHAR(200),
    password            VARCHAR(500) NOT NULL,
    email               VARCHAR(200),
    phone               VARCHAR(200),
    address             VARCHAR(500),
    status              VARCHAR(200),
    password_changed_on TIMESTAMP,
    is_enabled          BOOLEAN      NOT NULL,
    created_on          TIMESTAMP DEFAULT NOW(),
    updated_on          TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_to_role
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    status  VARCHAR(200),

    FOREIGN KEY (user_id) REFERENCES user_info (id) ON DELETE NO ACTION,
    FOREIGN KEY (role_id) REFERENCES user_role (id) ON DELETE NO ACTION
);


/* Inserting initial data */

INSERT INTO public.user_role(name, description, category)
VALUES ('admin', 'super user role', 'administration')
     , ('customer', 'client', 'customer');

INSERT INTO public.user_info(login, first_name, last_name, password, email, phone, address, status,
                             password_changed_on,
                             is_enabled, created_on, updated_on)
VALUES ('Gigant', 'Jack', 'London', 'qwerty', 'jl@email.ru', '+375294324302', 'Str. Yakubovskogo 34-35', 'Married',
        CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
     , ('Logan', 'Hew', 'Jackamn', 'holliwood', 'rossomaha@gmail.com', '+375292632402', 'Str. Lincoln 42-2',
        'Married',
        CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.users(username, password, roles)
VALUES ('Gigant', 'qwerty', 'customer')
     , ('Logan', 'holliwood', 'customer');

INSERT INTO public.user_to_role(user_id, role_id, status)
VALUES (1, 1, 'Active')
     , (2, 1, 'Inactive');