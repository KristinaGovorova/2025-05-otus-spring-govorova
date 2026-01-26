INSERT INTO authors (name) VALUES ('Stephen King');
INSERT INTO authors (name) VALUES ('H.P. Lovecraft');

INSERT INTO genres (name) VALUES ('Fantasy');
INSERT INTO genres (name) VALUES ('Horror');

INSERT INTO books (title, author_id, genre_id) VALUES ('The Shining', 1, 2);
INSERT INTO books (title, author_id, genre_id) VALUES ('The Call of Cthulhu', 2, 2);
INSERT INTO books (title, author_id, genre_id) VALUES ('The Dark Tower', 1, 1);