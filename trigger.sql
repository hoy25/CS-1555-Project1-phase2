--addForestCoverage
CREATE OR REPLACE FUNCTION add_forest_coverage()
RETURNS TRIGGER AS $$
        DECLARE
            state_area INTEGER;
            overlap_area INTEGER;
            state_rec STATE%rowtype;
        BEGIN
           state_area := (NEW.MBR_XMax - NEW.MBR_XMin) * (NEW.MBR_YMax - NEW.MBR_YMin);

           FOR state_rec IN (SELECT *
            FROM STATE)
            LOOP
               overlap_area := GREATEST(0, LEAST(NEW.MBR_XMax, state_rec.MBR_XMax) - GREATEST(NEW.MBR_XMin, state_rec.MBR_XMin)) *
                               GREATEST(0, LEAST(NEW.MBR_YMax, state_rec.MBR_YMax) - GREATEST(NEW.MBR_YMin, state_rec.MBR_YMin));
                IF state_area > 0 THEN
                    INSERT INTO COVERAGE(forest_no, state, percentage, area)
                    VALUES (NEW.fores_no, state_rec.name, (overlap_area/state_area)*100, overlap_area);
                end if;
               END LOOP;
        RETURN NEW;
        END;
    $$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS  add_forest_coverage ON FOREST;
CREATE CONSTRAINT TRIGGER add_forest_coverage
    AFTER INSERT OR UPDATE ON FOREST
    FOR EACH ROW
    EXECUTE FUNCTION add_forest_coverage();






--checkMaintainerEmployment
CREATE OR REPLACE FUNCTION check_maintainer_employment()
RETURNS TRIGGER AS $$
        DECLARE
        maintainer_state TEXT;
        sensor_location_state TEXT;
        BEGIN
        --get the state of new maintainer
        SELECT state INTO maintainer_state
        FROM EMPLOYED
        WHERE worker=NEW.maintainer_id;
        -- get sensors new location state
        SELECT STATE INTO sensor_location_state
        FROM COVERAGE
        WHERE forest_no = NEW.forest_no;

        -- check if maintainers stae covers new location
        IF maintainer_state IS DISTINCT FROM sensor_location_state THEN
            RAISE NOTICE 'The maintainer of this sensor is not employed by a state which covers the sensor. This operation has been reverted.';
            RAISE EXCEPTION 'The maintainer of this sensor is not employed by a state which covers the sensor. This operation has been reverted.';
        END IF;

        RETURN NEW;
        END;
    $$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS maintainer_employment_covreage_trigger ON SENSOR;
CREATE CONSTRAINT TRIGGER maintainer_employment_covreage_trigger
    AFTER INSERT OR UPDATE ON SENSOR
    FOR EACH ROW
    EXECUTE FUNCTION check_maintainer_employment()
