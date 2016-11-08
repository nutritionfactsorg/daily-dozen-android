ALTER TABLE foods ADD COLUMN id_name TEXT;
CREATE UNIQUE INDEX IF NOT EXISTS index_foods_id_name ON foods (id_name);