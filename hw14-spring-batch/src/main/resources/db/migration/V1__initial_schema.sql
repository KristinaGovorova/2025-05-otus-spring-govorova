CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL
);

CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       publication_year INT,
                       author_id BIGINT REFERENCES authors(id),
                       genre_id BIGINT REFERENCES genres(id)

);

INSERT INTO authors (name) VALUES
                                    ('Александр Пушкин'),
                                    ('Лев Толстой');

INSERT INTO genres (name) VALUES
                              ('Поэзия'),
                              ('Роман');

INSERT INTO books (title, author_id, genre_id) VALUES
                                                   ('Евгений Онегин', 1, 1),
                                                   ('Капитанская дочка', 1, 2),
                                                   ('Война и мир', 2, 2);
