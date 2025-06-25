-- Заполнение таблицы MPA
MERGE INTO mpa (id, name) VALUES (1, 'G');
MERGE INTO mpa (id, name) VALUES (2, 'PG');
MERGE INTO mpa (id, name) VALUES (3, 'PG-13');
MERGE INTO mpa (id, name) VALUES (4, 'R');
MERGE INTO mpa (id, name) VALUES (5, 'NC-17');

-- Заполнение таблицы жанров
MERGE INTO genres (id, name) VALUES (1, 'Комедия');
MERGE INTO genres (id, name) VALUES (2, 'Драма');
MERGE INTO genres (id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (id, name) VALUES (4, 'Триллер');
MERGE INTO genres (id, name) VALUES (5, 'Документальный');
MERGE INTO genres (id, name) VALUES (6, 'Боевик');

-- Заполнение таблицы пользователей
MERGE INTO users (id, email, login, name, birthday)
    VALUES (1, 'user1@example.com', 'user1', 'Иван Иванов', '1990-01-01');

MERGE INTO users (id, email, login, name, birthday)
    VALUES (2, 'user2@example.com', 'user2', 'Петр Петров', '1995-05-15');

MERGE INTO users (id, email, login, name, birthday)
    VALUES (3, 'user3@example.com', 'user3', 'Сидор Сидоров', '1985-10-20');

-- Заполнение таблицы фильмов
MERGE INTO films (id, name, description, release_date, duration, mpa_id)
    VALUES (1, 'Фильм 1', 'Описание фильма 1', '2020-01-01', 120, 3);

MERGE INTO films (id, name, description, release_date, duration, mpa_id)
    VALUES (2, 'Фильм 2', 'Описание фильма 2', '2021-02-15', 90, 2);

MERGE INTO films (id, name, description, release_date, duration, mpa_id)
    VALUES (3, 'Фильм 3', 'Описание фильма 3', '2019-05-20', 150, 4);

-- Связи фильмов и жанров
MERGE INTO film_genres (film_id, genre_id) VALUES (1, 1);
MERGE INTO film_genres (film_id, genre_id) VALUES (1, 2);
MERGE INTO film_genres (film_id, genre_id) VALUES (2, 3);
MERGE INTO film_genres (film_id, genre_id) VALUES (3, 4);
MERGE INTO film_genres (film_id, genre_id) VALUES (3, 6);

-- Заполнение таблицы дружбы
MERGE INTO friends (id, user_id, friend_id, status)
    VALUES (1, 1, 2, 'подтвержденная');

MERGE INTO friends (id, user_id, friend_id, status)
    VALUES (2, 1, 3, 'неподтвержденная');

-- Заполнение таблицы лайков
MERGE INTO likes (id, film_id, user_id, created_date)
    VALUES (1, 1, 1, '2023-01-01 12:00:00');

MERGE INTO likes (id, film_id, user_id, created_date)
    VALUES (2, 1, 2, '2023-01-02 13:30:00');

MERGE INTO likes (id, film_id, user_id, created_date)
    VALUES (3, 2, 1, '2023-01-03 14:45:00');