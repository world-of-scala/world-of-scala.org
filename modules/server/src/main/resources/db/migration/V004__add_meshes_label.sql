---
--- Add a label column to the meshes table
---
ALTER TABLE meshes
    ADD COLUMN label text NOT NULL;

