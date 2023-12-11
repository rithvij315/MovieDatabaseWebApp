DROP PROCEDURE IF EXISTS addMovieXML;

CREATE PROCEDURE addMovieXML(
    IN p_title VARCHAR(100),
    IN p_year INT,
    IN p_director VARCHAR(100)
)
BEGIN
    DECLARE last_movie_id VARCHAR(10);
    DECLARE new_movie_id VARCHAR(10);

    IF NOT EXISTS (SELECT 1
                   FROM movies
                   WHERE UPPER(title) = UPPER(p_title) AND
                           year = p_year AND
                           UPPER(director) = UPPER(p_director)) THEN

        SELECT movieId INTO last_movie_id FROM newids;
        SET new_movie_id = CONCAT('tt', LPAD(SUBSTRING(last_movie_id, 3) + 1, 7, '0'));
        INSERT INTO movies (id, title, year, director)
        VALUES (new_movie_id, p_title, p_year, p_director);

        UPDATE newids SET movieId = new_movie_id WHERE id=1;

        INSERT INTO ratings (movieId, rating, numVotes)
        VALUES (new_movie_id, 0, 0);

        SELECT new_movie_id;
    END IF;
END;