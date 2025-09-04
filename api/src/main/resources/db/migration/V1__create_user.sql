CREATE TABLE IF NOT EXISTS students.users
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(30),
    last_name VARCHAR(30),
    user_name VARCHAR(30) NOT NULL
);
