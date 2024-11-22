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

