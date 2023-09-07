CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS item_name_idx ON items
    USING GIN (lower("name") gin_trgm_ops);

CREATE INDEX IF NOT EXISTS item_description_idx ON items
    USING GIN (lower(description) gin_trgm_ops);