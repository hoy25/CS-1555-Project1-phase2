SET SCHEMA 'schema';

-- 1. addForest
--Given a name, area, acid level, MBR XMin, MBR XMax, MBR YMin, and MBR YMax, add
--a new forest to the system by inserting a new entry into the forest relation.
create or replace procedure addForest(

     name VARCHAR(30),
     area INTEGER,
     acid_level REAL,
     MBR_XMin REAL,
     MBR_XMax REAL,
     MBR_YMin REAL,
     MBR_YMax REAL) AS $$
        declare
            newForest_no integer;
        begin
            select MAX(forest_no) into newForest_no FROM forest;
            insert into FOREST
            Values(newForest_no+1, name, area, acid_level, MBR_XMin, MBR_XMax, MBR_YMin, MBR_YMax);
    end;
    $$ language plpgsql;

--operation 2: addTreeSpecies
------------------------
--Given a genus, epithet, ideal temperature, largest height, and raunkiaer life form
--add a new tree species to the system by inserting a new entry into the tree species relation
------------------------
CREATE OR REPLACE PROCEDURE addTreeSpecies
    (genus VARCHAR(30),
    epithet VARCHAR(30),
    ideal_temperature REAL,
    largest_height REAL,
    raunkiaer_life_form LIFE_FORM)
AS $$
BEGIN
    INSERT INTO tree_species
    VALUES (genus,epithet,ideal_temperature,largest_height,raunkiaer_life_form);
end;
$$ LANGUAGE plpgsql;

--3. addSpeciesToForest
--Given a forest no, genus, and epithet, add an entry for the tree species being found in the
--forest

create or replace procedure addSpeciesToFores(forest_no integer, genus VARCHAR(30),epithet VARCHAR(30)) as $$
    begin
        insert into FOUND_IN
        values (forest_no, genus, epithet);
    end;
    $$ language plpgsql;

--operation 4: newWorker
------------------------
--Given a SSN, First name, Last name, Middle initial, rank, and a state abbreviation where the worker will initially be employed
--add a new worker to the system by inserting a new entry into the worker relation.
--In addition, the worker’s employment should be inserted.
-- Hint: While debugging, you may want to use transactions.
------------------------
CREATE OR REPLACE PROCEDURE newWorker
    (SSN VARCHAR(9),
    first VARCHAR(30),
    last VARCHAR(30),
    middle CHAR(1),
    rank RANK_ORDER,
    abbreviation CHAR(2)
    )
AS $$
BEGIN
    --WORKER TABLE
    INSERT INTO WORKER
    VALUES (SSN,first,last,middle,rank);
    --EMPLOYED TABLE
    INSERT INTO EMPLOYED
    VALUES (abbreviation,SSN);

END;
$$LANGUAGE plpgsql;

--5. employWorkerToState
--Given a state abbreviation and worker SSN, add an entry for the worker being employed by
--the state.

create or replace PROCEDURE employWorkerToState(abb CHAR(2),  SSN VARCHAR(9)) AS
    $$
    declare
        storedState real;
    begin
        select abbreviation from STATE
            WHERE STATE.abbreviation = abb INTO storedState;
        insert into EMPLOYED
            values(storedState, SSN);
    end;
    $$ language plpgsql;

--operation 6: placeSensor
------------------------
--Given the energy of the sensor, location of deployment (X, Y), and the maintainer id,
--add a new sensor to the system by inserting a new entry into the sensor relation.
--Note that for the last charged and last read timestamps,
--you should use the current synthetic time from the CLOCK relation.
------------------------
CREATE OR REPLACE PROCEDURE placeSensor
    (
    energy INTEGER,
    X REAL,
    Y REAL,
    maintainer_id VARCHAR(9)
    )
AS $$
DECLARE
    NEW_sensor_id INTEGER;
    last_charged TIMESTAMP;
    last_read TIMESTAMP;

BEGIN
    SELECT MAX(sensor_id) INTO NEW_sensor_id
    FROM SENSOR;
    SELECT synthetic_time INTO last_charged
    FROM CLOCK;
    SELECT synthetic_time INTO last_read
    FROM CLOCK;

    INSERT INTO SENSOR (sensor_id, last_charged, energy, last_read, x, y, maintainer_id)
    VALUES (NEW_sensor_id+1,last_charged,energy,last_read,X,Y,maintainer_id);

END;
$$LANGUAGE plpgsql;

--7. generateReport
--Given the sensor id generating the report, the report time, and the temperature recorded by
--the sensor, add a new report to the system by inserting a new entry into the report relation.
CREATE OR REPLACE PROCEDURE generateReport
    (sensor_id INTEGER,
    report_time timestamp,
    record_temp real)
AS $$
BEGIN
    INSERT INTO REPORT
    VALUES (sensor_id,report_time,record_temp);
end;
$$ LANGUAGE plpgsql;

--operation 8: removeSpeciesFromForest
------------------------
--Given a genus, epithet, and a forest no
--remove the tree species from the forest
------------------------
CREATE OR REPLACE PROCEDURE removeSpeciesFromForest
    (r_genus VARCHAR(30),
    r_epithet VARCHAR(30),
    r_forest_no INTEGER
    )
AS $$
BEGIN
    --FOUND_IN TABLE
    DELETE FROM FOUND_IN
    WHERE genus = r_genus AND epithet = r_epithet AND forest_no = r_forest_no;

END;
$$LANGUAGE plpgsql;

--9. deleteWorker
--Given a SSN, remove the worker by deleting the worker from the worker relation. In addition,
--the worker should no longer be employed by any states and all sensors maintained by the worker
--should be removed.

create or replace PROCEDURE deleteWorker(WORKER_SSN INTEGER) as $$

    begin
        DELETE from WORKER
        WHERE SSN = WORKER_SSN;

        DELETE FROM EMPLOYED
        WHERE WORKER = WORKER_SSN;

        DELETE FROM SENSOR
        WHERE maintainer_id = WORKER_SSN;
    end;
    $$ language plpgsql;

--operation 10: moveSensor
------------------------
--Given a sensor id and location (X, Y)
--move the deployed sensor by updating the sensor in the sensor relation.
------------------------
CREATE OR REPLACE PROCEDURE moveSensor
    (m_sensor_id INTEGER,
    m_X REAL,
    m_Y REAL
    )
AS $$
BEGIN
    --sensor TABLE
    UPDATE SENSOR
    SET X = m_X, Y = m_Y
    WHERE sensor_id = m_sensor_id;

END;
$$LANGUAGE plpgsql;

--11. removeWorkerFromState
--Given a SSN and state abbreviation, remove the worker’s employment for the given state. In
--addition, if the worker is maintaining a sensor that is located within the state where they were
--removed from, those sensor(s) should be reassigned to another worker within that same state
--who has the lowest SSN. However, if no other workers are employed to that state, the sensor
--should be removed.

create or replace procedure  removeWorkerFromState(r_SSN Integer, r_abbreviation char(2) )
    as $$

    DECLARE
        new_maintainer_id varchar(9);
      

    begin
        DELETE FROM EMPLOYED
            WHERE worker = r_snn AND
                  abbreviation = r_abbreviation;
        SELECT min(maintainer_id) FROM EMPLOYED
            WHERE abbreviation = r_abbreviation into new_maintainer_id;
        IF new_maintainer_id IS NOT NULL THEN
            update SENSOR
                set maintainer_id = new_maintainer_id
                WHERE maintainer_id = r_SNN;

        ELSE
            delete from SENSOR
            where new_maintainer_id = r_SSN;
        end if;
    end;
$$ language plpgsql;

--operation 12: removeSensor
------------------------
--Given a sensor id
--remove the sensor by deleting the sensor from the sensor relation
--In addition, any reports generated by the sensor should be removed.
------------------------
CREATE OR REPLACE PROCEDURE removeSensor(r_sensor_id INTEGER)
AS $$
BEGIN
    --REPORT TABLE
    DELETE FROM REPORT
    WHERE sensor_id = r_sensor_id;
    --SENSOR TABLE
    DELETE FROM SENSOR
    WHERE sensor_id = r_sensor_id;


END;
$$LANGUAGE plpgsql;

--13. listSensors
--Given a forest id, display all sensors within the specified forest.
create or replace function listSensors(l_forest_id integer)
    returns table (
        sensor_id INTEGER,
        last_charged timestamp,
        energy INTEGER,
        last_read timestamp,
        X real,
        Y real,
        maintainer_id VARCHAR(9)
        ) as $$
        BEGIN
            Return query
            SELECT
                S.sensor_id,
                S.last_charged,
                S.energy,
                S.last_read,
                S.x,
                S.Y,
                S.maintainer_id
            FROM SENSOR S JOIN
                EMPLOYED E ON S.maintainer_id = E.WORKER
            JOIN
                COVERAGE C ON E.abbreviation = C.abbreviation AND C.forest_no  = l_forest_id;
        end;
    $$ language plpgsql;

--operation 14: listMaintainedSensors
------------------------
--Given a worker’s SSN
--display all sensors that the worker is currently maintaining.
------------------------

CREATE OR REPLACE FUNCTION listMaintainedSensors (worker_SSN char(9))
    RETURNS TABLE (
        sensor_id INTEGER,
        last_charged timestamp,
        energy INTEGER,
        last_read timestamp,
        X real,
        Y real,
        maintainer_id VARCHAR(9)
                  )
AS $$
BEGIN
    RETURN QUERY
    SELECT sensor_id, last_charged,energy,last_read,X,Y,maintainer_id
    FROM SENSOR
    WHERE  maintainer_id = worker_SSN;
end;
$$ LANGUAGE plpgsql;

--15. locateTreeSpecies
--Find all forests that contain any tree species whose genus matches the pattern α or epithet
--matches the pattern β.
create or replace function locateTreeSpecies(pattern_alpha real, pattern_beta real)
    RETURNS TABLE
            (
                forest_id integer
            ) as $$

    BEGIN
        return query
        select forest_id
        from FOUND_IN
        WHERE genus LIKE pattern_alpha
        OR epithet LIKE pattern_beta;
end;
$$ language plpgsql;
