CREATE TABLE IF NOT EXISTS students.language
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(30)
);

CREATE TABLE students.level
(
    id     SERIAL PRIMARY KEY,
    code   VARCHAR(20) UNIQUE NOT NULL,
    number INT UNIQUE         NOT NULL
);

CREATE TABLE IF NOT EXISTS students.grade
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES students.users (id),
    language_id INT REFERENCES students.language (id),
    level_id    INT REFERENCES students.level (id)
);