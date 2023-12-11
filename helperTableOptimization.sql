CREATE TABLE newids (
    id INTEGER PRIMARY KEY,
    movieId VARCHAR(10),
    starId VARCHAR(10),
    genreId INTEGER
);

INSERT INTO newids (id, movieId, starId, genreId)
SELECT 1, (SELECT MAX(id) FROM movies) AS maxMovieId, (SELECT MAX(id) FROM stars) AS maxStarId, (SELECT MAX(id) FROM genres) AS maxGenreId;