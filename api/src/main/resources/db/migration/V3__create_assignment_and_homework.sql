CREATE TABLE IF NOT EXISTS students.assignment
(
    id          BIGSERIAL PRIMARY KEY,
    language_id INT REFERENCES students.language (id),
    level_id    INT REFERENCES students.level (id),
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS students.homework
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT REFERENCES students.users (id),
    assigment_id BIGINT REFERENCES students.assignment (id),
    status       VARCHAR(15) NOT NULL
);