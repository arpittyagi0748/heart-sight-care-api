-- Run this script to create the schema in PostgreSQL
-- You can execute this using your PostgreSQL client (pgAdmin, DBeaver, etc.)
-- or via command line if psql is available

-- IMPORTANT: PostgreSQL converts unquoted identifiers to lowercase
-- So we use lowercase schema name to avoid confusion

-- Create the schema (lowercase)
CREATE SCHEMA IF NOT EXISTS TestBackendProject;

-- Verify schema creation
SELECT schema_name 
FROM information_schema.schemata 
WHERE schema_name = 'TestBackendProject';

-- Grant necessary permissions (if needed)
GRANT ALL ON SCHEMA TestBackendProject TO postgres;
GRANT ALL ON ALL TABLES IN SCHEMA TestBackendProject TO postgres;
GRANT ALL ON ALL SEQUENCES IN SCHEMA TestBackendProject TO postgres;

-- If you already created "TestBackendProject" (mixed case), you can drop it:
-- DROP SCHEMA IF EXISTS "TestBackendProject" CASCADE;
