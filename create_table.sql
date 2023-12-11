-- CREATE TABLES
CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;
create table movies(
    id varchar(10) PRIMARY KEY,
    title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL,
    FULLTEXT idx (title)
);

create table stars(
    id varchar(10) PRIMARY KEY,
    name varchar(100) NOT NULL,
    birthYear integer
);

create table stars_in_movies(
    starId varchar(10) NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table genres(
    id integer PRIMARY KEY AUTO_INCREMENT,
    name varchar(32) NOT NULL
);

create table genres_in_movies(
    genreId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table creditcards(
    id varchar(20) PRIMARY KEY,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);

create table customers(
    id integer PRIMARY KEY AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) NOT NULL,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL,
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

create table sales(
    id integer PRIMARY KEY AUTO_INCREMENT,
    customerId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    saleDate date NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table ratings(
    movieId varchar(10) NOT NULL,
    rating float NOT NULL,
    numVotes integer NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- New Stuff
create table employees (
    email varchar(50) primary key,
    password varchar(20) NOT NULL,
    fullname varchar(100)
);

INSERT INTO employees VALUES ('classta@email.edu', 'classta', 'TA CS122B');

CREATE TABLE newids (
    id int primary key,
    movieId varchar(10),
    starId varchar(10),
    genreId int
);

INSERT INTO newids (id, movieId, starId, genreId)
SELECT 1, (SELECT MAX(id) FROM movies) AS maxMovieId, (SELECT MAX(id) FROM stars) AS maxStarId, (SELECT MAX(id) FROM genres) AS maxGenreId;

-- Run create database sql file
-- Run password encyptions
-- Run helperTableOptimization.sql
-- Run add-star.sql, stored-procedure.sql, and all files in XMLProcedures


