
INSERT INTO students.users(id, name, last_name, user_name)
VALUES
    (1, 'Misha', 'Mikhailovich', '@Mishelby'),
    (2, 'Anna', 'Ivanova', '@AnnaI'),
    (3, 'Petr', 'Petrov', '@PetyaP'),
    (4, 'Olga', 'Sidorova', '@OlgaS'),
    (5, 'Dmitry', 'Kuznetsov', '@DimaK');

INSERT INTO students.language(id, name)
VALUES
    (1, 'Java'),
    (2, 'Python'),
    (3, 'JavaScript');

INSERT INTO students.level(id, code, number)
VALUES
    (1, 'BEGINNER', 1),
    (2, 'MIDDLE', 2),
    (3, 'SENIOR', 3);

INSERT INTO students.grade(id, user_id, language_id, level_id)
VALUES
    (1, 1, 1, 1),
    (2, 1, 1, 2),
    (3, 2, 1, 2),
    (4, 2, 3, 1),
    (5, 3, 2, 1),
    (6, 3, 3, 2),
    (7, 4, 1, 3),
    (8, 5, 2, 2);