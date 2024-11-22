CREATE TABLE IF NOT EXISTS "users"(
    id bigserial PRIMARY KEY,
    firstname text NOT NULL,
    lastname text NOT NULL,
    email text NOT NULL UNIQUE,
    hashed_password text NOT NULL,
    creation_date timestamp NOT NULL
);

