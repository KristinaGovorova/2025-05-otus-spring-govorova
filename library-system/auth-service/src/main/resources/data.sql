TRUNCATE TABLE users RESTART IDENTITY CASCADE;

INSERT INTO users(username, password, role) VALUES
    ('admin', '$2a$12$3Ix25k4n9HW.R1PIK6pXGuU4f.tE5zrLH3HjuqnmPCg8XnRP2myxC', 'ROLE_ADMIN');

INSERT INTO users(username, password, role) VALUES
    ('username', '$2a$12$3Ix25k4n9HW.R1PIK6pXGuU4f.tE5zrLH3HjuqnmPCg8XnRP2myxC', 'ROLE_USER');

INSERT INTO users(username, password, role) VALUES
    ('user', '$2a$12$3Ix25k4n9HW.R1PIK6pXGuU4f.tE5zrLH3HjuqnmPCg8XnRP2myxC', 'ROLE_USER');