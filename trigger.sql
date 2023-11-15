--addForestCoverage
CREATE OR REPLACE FUNCTION add_forest_coverage()
RETURNS TRIGGER AS $$
        DECLARE
            state_area real;
            overlap_area real;
            x_Area real;
            y_Area real;
            state_rec record;
        BEGIN
           state_area := (NEW.MBR_XMax - NEW.MBR_XMin) * (NEW.MBR_YMax - NEW.MBR_YMin);

           FOR state_rec IN (SELECT *
            FROM STATE)
            LOOP
               IF NEW.MBR_XMin < state_rec.MBR_XMax OR state_rec.MBR_XMin < NEW.MBR_XMax THEN
                   --nothing

                ELSIF NEW.MBR_YMin > state_rec.MBR_YMax or state_rec.MBR_YMin > NEW.MBR_YMax THEN
                    --nothing

                ELSE
                    x_Area := LEAST(NEW.MBR_XMax, state_rec.MBR_XMax) - GREATEST(NEW.MBR_XMin, state_rec.MBR_XMin);
                    y_Area := LEAST(NEW.MBR_YMax, state_rec.MBR_YMax) - GREATEST(NEW.MBR_YMin, state_rec.MBR_YMin);
                    overlap_area := x_Area * y_Area;

                    IF state_area > 0 THEN
                        INSERT INTO COVERAGE(forest_no, abbreviation, percentage, area)
                        VALUES (NEW.forest_no, state_rec.abbreviation, (overlap_area/state_rec.area)*100, overlap_area);

                    end if;
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
        sensor_location_state char(2);
        state_rec STATE%rowtype;
        maintainer_state char(2);
        BEGIN
        --get the state of new Sensor

        select abbreviation INTO maintainer_state
            FROM EMPLOYED
            WHERE worker = NEW.maintainer_id;

        FOR state_rec IN(SELECT * FROM STATE)
        LOOP
            IF NEW.X > state_rec.MBR_XMin AND NEW.X < state_rec.MBR_XMax AND NEW.Y < state_rec.MBR_YMax AND NEW.Y > state_rec.MBR_YMin THEN
                sensor_location_state := state_rec.abbreviation;
            end if;
        end loop;

     --check if maintainers stae covers new location
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
