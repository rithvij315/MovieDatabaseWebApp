DROP PROCEDURE IF EXISTS add_movie;

CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INT,
    IN p_director VARCHAR(100),
    IN p_star_name VARCHAR(100),
    IN p_birth_year int,
    IN p_genre VARCHAR(32)
)
BEGIN
    DECLARE last_movie_id VARCHAR(10);
    DECLARE new_movie_id VARCHAR(10);
    DECLARE last_star_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);
    DECLARE genre_id int;

    SELECT movieId INTO last_movie_id FROM newids;
    SET new_movie_id = CONCAT('tt', LPAD(SUBSTRING(last_movie_id, 3) + 1, 7, '0'));

    IF NOT EXISTS (SELECT 1
                   FROM movies
                   WHERE UPPER(title) = UPPER(p_title) AND
                         year = p_year AND
                         UPPER(director) = UPPER(p_director)) THEN
        INSERT INTO movies (id, title, year, director)
        VALUES (new_movie_id, p_title, p_year, p_director);

        UPDATE newids SET movieId = new_movie_id WHERE id=1;

        SELECT id INTO new_star_id FROM stars WHERE UPPER(name) = UPPER(p_star_name);

        IF new_star_id IS NULL THEN
            SELECT starId INTO last_star_id FROM newids;
            SET new_star_id = CONCAT('nm', LPAD(SUBSTRING(last_star_id, 3) + 1, 7, '0'));
            INSERT INTO stars (id, name, birthYear) VALUES (new_star_id, p_star_name, p_birth_year);
            UPDATE newids SET starId = new_star_id WHERE id=1;
        END IF;

        INSERT INTO stars_in_movies (starId, movieId)
        VALUES (new_star_id, new_movie_id);

        SELECT id INTO genre_id FROM genres WHERE UPPER(name) = UPPER(p_genre);

        IF genre_id IS NULL THEN
            SELECT genreId+1 INTO genre_id FROM newids;
            INSERT INTO genres (id, name) VALUES (genre_id, p_genre);
            UPDATE newids SET genreId = genre_id WHERE id=1;
        END IF;

        INSERT INTO genres_in_movies (genreId, movieId)
        VALUES (genre_id, new_movie_id);

        INSERT INTO ratings (movieId, rating, numVotes)
        VALUES (new_movie_id, 0, 0);

        SELECT new_movie_id, new_star_id, genre_id;
    END IF;
END;