--
-- Purpose: Add a location column to the organisations table.
-- The location column will be of type point and will store the latitude and longitude of the organisation.
-- This will allow us to perform spatial queries on the organisations table.
--
-- This illustrates how to add a new column to an existing table, then migrate data.
ALTER TABLE organisations
    ADD COLUMN location point;

UPDATE
    organisations
SET
    location = point(long, lat);

ALTER TABLE organisations
    DROP COLUMN lat;

ALTER TABLE organisations
    DROP COLUMN long;

ALTER TABLE organisations
    ALTER COLUMN location SET NOT NULL;

