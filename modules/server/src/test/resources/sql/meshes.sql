CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE meshes(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    label text NOT NULL,
    blob bytea,
    PRIMARY KEY (id)
);

INSERT INTO meshes(label, blob)
    VALUES ('cube', pg_read_file('/etc/resolv.conf')::bytea)
