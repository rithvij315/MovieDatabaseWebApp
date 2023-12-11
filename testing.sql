# SELECT * FROM movies;
# To Test SQL Queries Locally

SELECT g.name
FROM genres_in_movies AS gm, genres AS g, movies AS m
WHERE gm.genreId=g.id AND m.id=gm.movieId AND m.id='tt0120681';

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m, ratings AS r
ORDER BY r.rating DESC LIMIT 20;

SELECT s.name
FROM stars_in_movies AS sm, stars AS s, movies AS m
WHERE sm.starId=s.id AND m.id=sm.movieId AND m.id='tt0120681'
LIMIT 3;

SELECT s.id, s.name,
COALESCE(s.birthYear, 'N/A') as birthYear,
m.id, m.title, m.year, m.director from stars as s, stars_in_movies as sim, movies as m
WHERE m.id = sim.movieId AND sim.starId = s.id AND s.id = 'nm2367590';

SELECT c.email, c.password
FROM customers AS c
WHERE c.email=?;

SELECT g.name
FROM genres AS g;

SELECT m.id, m.title, m.year, m.director, r.rating, g.name
FROM movies AS m, ratings AS r, genres AS g, genres_in_movies AS gm
WHERE gm.genreId=g.id AND gm.movieId=m.id AND r.movieId=m.id AND g.id=11
LIMIT 100;

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m, ratings AS r
WHERE r.movieId=m.id AND m.title NOT REGEXP '^[a-zA-Z0-9]'
LIMIT 100;


SELECT s.name, COUNT(*) AS count
FROM stars_in_movies AS sm, stars AS s, movies AS m
WHERE sm.starId=s.id AND m.id=sm.movieId AND m.id='tt0120681'
GROUP BY s.name
ORDER BY count DESC, s.name
LIMIT 3;



SELECT tbl.name, tbl.count
FROM (SELECT s.name, s.id, COUNT(*) as count
    FROM stars_in_movies AS sm, stars AS s, movies AS m
    WHERE sm.starId=s.id AND m.id=sm.movieId
    GROUP BY s.name, s.id) AS tbl
WHERE tbl.id IN (SELECT starId
                 FROM stars_in_movies
                 WHERE movieId='tt0120667')
ORDER BY tbl.count DESC, tbl.name;


SELECT s.name
FROM stars_in_movies AS sm
         JOIN stars AS s ON sm.starId = s.id
         JOIN movies AS m ON m.id = sm.movieId
WHERE sm.starId IN (
    SELECT starId
    FROM stars_in_movies
    WHERE movieId = 'tt0120667')
GROUP BY s.name
ORDER BY COUNT(*) DESC, s.name
LIMIT 3;

ALTER TABLE movies
ADD cost DECIMAL(10, 2) NOT NULL DEFAULT 5.00;


SELECT c.id
FROM customers AS C, creditcards AS cc
WHERE cc.id=? AND cc.firstName=? AND cc.lastName=? AND cc.expiration=?;

INSERT INTO sales (customerId, movieId, saleDate)
VALUES ();

SELECT m.id, m.title, m.year, m.director, r.rating
FROM movies AS m, ratings AS r
WHERE r.movieId=m.id AND
      UPPER(m.title) LIKE '%' + ? + '%' AND m.year=? AND
      UPPER(m.director) LIKE '%' + ? + '%' AND m.id IN (
            SELECT sm.movieId
            FROM stars AS s, stars_in_movies AS sm
            WHERE s.id=sm.starId AND s.name LIKE '%' + ? + '%')
LIMIT 100;

SELECT sm.movieId
FROM stars AS s, stars_in_movies AS sm
WHERE s.id=sm.starId AND s.name LIKE '%' + ? + '%';

-- Creating employees table
create table employees (email varchar(50) primary key,password varchar(20) NOT NULL,fullname varchar(100)); INSERT INTO employees VALUES ('classta@email.edu', 'classta', 'TA CS122B');

create table customers_backup(id integer auto_increment primary key,firstName varchar(50) not null,lastName varchar(50) not null,ccId varchar(20) not null,address varchar(200) not null,email varchar(50) not null,password varchar(20) not null,foreign key(ccId) references creditcards(id));insert into customers_backup select * from customers;


SELECT table_name
FROM information_schema.tables
WHERE table_type='BASE TABLE'
  AND table_schema = 'moviedb';

SELECT COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'customers';

SELECT
    c.TABLE_NAME,
    c.COLUMN_NAME,
    c.DATA_TYPE
FROM
    INFORMATION_SCHEMA.COLUMNS c
ORDER BY
    c.TABLE_NAME,
    c.ORDINAL_POSITION;

SELECT
    t.TABLE_NAME,
    c.COLUMN_NAME,
    c.DATA_TYPE
FROM
    INFORMATION_SCHEMA.COLUMNS AS c, information_schema.tables AS t
WHERE c.TABLE_NAME=t.TABLE_NAME AND table_type='BASE TABLE' AND t.table_schema = 'moviedb';

SELECT UPPER(CONCAT(title, year, director)) as k, id FROM movies;
SELECT UPPER(CONCAT(name, IFNULL(birthYear, ''))) as k, id FROM stars;
SELECT UPPER(name) as k, id FROM genres;

INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?);
INSERT INTO ratings (movieId, rating, numVotes) VALUES (?, 0, 0);

INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?);
INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?);

INSERT INTO genres (id, name) VALUES (?, ?);
INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?);


# CALL add_movie('Inception', 2010, 'Christopher Nolan', 'Kirby Ammari', 'KirbyTime');

DELETE FROM stars_in_movies
WHERE movieId >= 'tt0499470';

DELETE FROM genres_in_movies
WHERE movieId >= 'tt0499470';

DELETE FROM stars
WHERE id >= 'nm9423085';

DELETE FROM genres
WHERE id >= 24;

DELETE FROM ratings
WHERE movieId >= 'tt0499470';

DELETE FROM movies
WHERE id >= 'tt0499470';

DELETE FROM newids
WHERE id = 1;

INSERT INTO newids (id, movieId, starId, genreId)
SELECT 1, (SELECT MAX(id) FROM movies) AS maxMovieId, (SELECT MAX(id) FROM stars) AS maxStarId, (SELECT MAX(id) FROM genres) AS maxGenreId;

# INSERT INTO genres(id, name) VALUES (24, 'TestKirby');

ALTER TABLE movies ADD FULLTEXT(title);

SELECT m.id, m.title, m.year, r.rating FROM movies AS m, ratings AS r
WHERE r.movieId=m.id AND MATCH(title) AGAINST (? IN BOOLEAN MODE);