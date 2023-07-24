DELETE
FROM film_genres;

DELETE
FROM film_likes;

DELETE
FROM films;

ALTER TABLE films ALTER COLUMN id RESTART WITH 1;

DELETE
FROM friends;

DELETE
FROM genres;

DELETE
FROM mpa;

DELETE
FROM users;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

MERGE INTO genres (id, name)
VALUES (1, 'Комедия'),
(2,'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO mpa (id, mpa)
VALUES (1, 'G'),
(2,'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');