CREATE TABLE IF NOT EXISTS students.chat
(
    id          BIGSERIAL PRIMARY KEY,
    chat_number VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS students.users
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(30),
    last_name VARCHAR(30),
    user_name VARCHAR(30) NOT NULL,
    chat_id   BIGINT UNIQUE REFERENCES students.chat (id)
);
