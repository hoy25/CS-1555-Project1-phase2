SET SCHEMA 'schema';
---------------------------------------------
--schema for proejct 1 phase 2 part 2
--author: Hongkun Yao, Adam Sheelar
---------------------------------------------

--1. rankForestSensors
--rank forest by sensor_num
--add rank
DROP VIEW IF EXISTS Forest_Sensor;
CREATE OR REPLACE VIEW rankForestSensors AS
SELECT F.forest_no, count(S.sensor_id) as sensor_num
FROM FOREST F
JOIN SENSOR S ON (S.X BETWEEN F.MBR_XMIN AND F.MBR_XMAX)
                   AND(S.Y BETWEEN F.MBR_YMIN AND F.MBR_YMAX)
GROUP BY F.forest_no;



CREATE OR REPLACE FUNCTION rankForestSensors()
    RETURNS TABLE(
        forest_no INTEGER,
        rank BIGINT
    )
AS $$
    BEGIN RETURN QUERY
    SELECT r1.forest_no,rank() OVER(ORDER BY r1.sensor_num DESC)
    FROM rankForestSensors r1;
END;
$$LANGUAGE plpgsql;


--2.habitableEnvironment
CREATE OR REPLACE FUNCTION habitableEnvironment(
    input_genus VARCHAR(30),
    input_epithet VARCHAR(30),
    input_k INTEGER
) RETURNS TABLE (
    forest_no INTEGER
) AS $$
DECLARE
    calculated_time TIMESTAMP;
BEGIN
    -- Calculate the timestamp based on the given number of years
    calculated_time := (SELECT MAX(synthetic_time) - interval '1 year' * input_k FROM CLOCK);

    RETURN QUERY
    SELECT
        c.forest_no
    FROM
        REPORT r
    INNER JOIN
        SENSOR s ON r.sensor_id = s.sensor_id
    INNER JOIN
        EMPLOYED e ON s.maintainer_id = e.worker
    INNER JOIN
        STATE st ON e.abbreviation = st.abbreviation
    INNER JOIN
        COVERAGE c ON st.abbreviation = c.state
    INNER JOIN
        TREE_SPECIES ts ON ts.genus = input_genus AND ts.epithet = input_epithet
    WHERE
        r.report_time >= calculated_time
    GROUP BY
        c.forest_no, ts.ideal_temperature
    HAVING
        ABS(ts.ideal_temperature - AVG(r.temperature)) <= 5;
END;
$$ LANGUAGE plpgsql;

--3.topSensors
DROP FUNCTION IF EXISTS topSensors(K INTEGER, X_month INTEGER);
CREATE OR REPLACE FUNCTION topSensors(K INTEGER, X_month INTEGER)
    RETURNS TABLE(
        sensor_id INTEGER,
        report_num BIGINT
                 )
AS $$
DECLARE
    start_time TIMESTAMP;
    curr_time TIMESTAMP;
BEGIN
    --Find time interval
    SELECT MAX(synthetic_time) INTO curr_time --SHOULD SELECT MAX?
    FROM CLOCK;
    SELECT curr_time -INTERVAL '30 days' * X_month INTO start_time
    FROM CLOCK;

    RETURN QUERY
    SELECT r.sensor_id, COUNT(r.sensor_id) AS report_num
    FROM REPORT r
    WHERE r.report_time BETWEEN start_time AND curr_time
    GROUP BY r.sensor_id
    ORDER BY report_num DESC
    LIMIT K;

END;
$$LANGUAGE plpgsql;



--4.threeDegrees
DROP FUNCTION IF EXISTS threeDegrees(s_forest INTEGER, e_forest INTEGER);
CREATE OR REPLACE FUNCTION threeDegrees(s_forest INTEGER, e_forest INTEGER)
RETURNS TEXT AS $$
DECLARE
    result_path TEXT;
BEGIN
    WITH RECURSIVE HopPath AS (
        SELECT
            f1.forest_no AS start_forest,
            f1.forest_no AS current_forest,
            f1.forest_no::TEXT AS path,
            1 AS hop,
            (f1.forest_no = e_forest) AS foundpath
        FROM
            FOUND_IN f1
        WHERE
            f1.forest_no = s_forest

        UNION ALL

        SELECT
            hp.start_forest,
            f2.forest_no AS current_forest,
            hp.path || ' --> ' || f2.forest_no::TEXT,
            hp.hop + 1,
            (f2.forest_no = e_forest) AS foundpath
        FROM
            HopPath hp
        JOIN
            FOUND_IN f1 ON hp.current_forest = f1.forest_no
        JOIN
            FOUND_IN f2 ON f1.genus = f2.genus AND f1.epithet = f2.epithet
        WHERE
            hp.path NOT LIKE '%' || f2.forest_no::TEXT || '%'
            AND hp.hop < 4
            AND NOT hp.foundpath
    )
    SELECT
        path
    FROM
        HopPath
    WHERE
        foundpath
    ORDER BY
        hop
    LIMIT 1 INTO result_path;

    IF result_path IS NOT NULL THEN
        RETURN result_path;
    ELSE
        RETURN 'No path found';
    END IF;
END;
$$ LANGUAGE plpgsql;

