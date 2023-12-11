DROP PROCEDURE IF EXISTS addStarXML;

CREATE PROCEDURE addStarXML(
    IN movie_id VARCHAR(10),
    IN star_name VARCHAR(100),
    IN star_dob INTEGER
)
BEGIN
    DECLARE last_star_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);

    SELECT id INTO new_star_id FROM stars WHERE UPPER(name) = UPPER(star_name) AND star_dob=birthYear;

    IF new_star_id IS NULL THEN
        SELECT starId INTO last_star_id FROM newids;
        SET new_star_id = CONCAT('nm', LPAD(SUBSTRING(last_star_id, 3) + 1, 7, '0'));
        INSERT INTO stars (id, name, birthYear) VALUES (new_star_id, star_name, star_dob);
        UPDATE newids SET starId = new_star_id WHERE id=1;
    END IF;

    INSERT INTO stars_in_movies (starId, movieId)
    VALUES (new_star_id, movie_id);

END;