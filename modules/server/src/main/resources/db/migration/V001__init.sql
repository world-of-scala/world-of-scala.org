CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "users"(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    firstname text NOT NULL,
    lastname text NOT NULL,
    email text NOT NULL UNIQUE,
    hashed_password text NOT NULL,
    creation_date timestamp NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE meshes(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    blob bytea,
    PRIMARY KEY (id)
);

CREATE TABLE organisations(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name text NOT NULL,
    meshes_id uuid REFERENCES meshes(id),
    lat float,
    long float,
    creation_date timestamp NOT NULL,
    PRIMARY KEY (id)
);

