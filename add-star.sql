DROP PROCEDURE IF EXISTS add_star;

CREATE PROCEDURE add_star(
    IN p_star_name VARCHAR(100),
    IN p_birth_year int
)
BEGIN
    DECLARE last_star_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);

    SELECT starId INTO last_star_id FROM newids;
    SET new_star_id = CONCAT('nm', LPAD(SUBSTRING(last_star_id, 3) + 1, 7, '0'));
    UPDATE newids SET starId = new_star_id WHERE id=1;

    INSERT INTO stars (id, name, birthYear)
    VALUES (new_star_id, p_star_name, p_birth_year);

    SELECT new_star_id;
END;