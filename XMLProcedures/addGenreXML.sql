DROP PROCEDURE IF EXISTS addGenreXML;

CREATE PROCEDURE addGenreXML(
    IN movie_id VARCHAR(10),
    IN genre_name VARCHAR(32)
)
BEGIN
    DECLARE genre_id int;
    SELECT id INTO genre_id FROM genres WHERE UPPER(name) = UPPER(genre_name);

    IF genre_id IS NULL THEN
        SELECT genreId+1 INTO genre_id FROM newids;
        INSERT INTO genres (id, name)
        VALUES (genre_id, genre_name);
        UPDATE newids SET genreId = genre_id WHERE id=1;
    END IF;

    INSERT INTO genres_in_movies (genreId, movieId)
    VALUES (genre_id, movie_id);
END;