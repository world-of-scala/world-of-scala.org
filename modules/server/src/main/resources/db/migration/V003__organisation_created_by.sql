--
-- Purpose: Add a location column to the organisations table.
-- The location column will be of type point and will store the latitude and longitude of the organisation.
-- This will allow us to perform spatial queries on the organisations table.
--
-- This illustrates how to add a new column to an existing table, then migrate data.
ALTER TABLE organisations
    ADD created_by uuid REFERENCES users(id);

UPDATE
    organisations
SET
    created_by =(
        SELECT
            id
        FROM
            users
        ORDER BY
            creation_date
        LIMIT 1);

ALTER TABLE organisations
    ALTER COLUMN created_by SET NOT NULL;

