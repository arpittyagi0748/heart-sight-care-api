-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS "TestBackendProject";

-- Set search path
SET search_path TO "TestBackendProject";

-- Create users table
CREATE TABLE IF NOT EXISTS "TestBackendProject".users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    password VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'INTERNAL',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Create index on phone for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);

-- Create index on role for filtering
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Insert default admin user (password: admin123)
-- BCrypt hash for 'admin123'
INSERT INTO users (full_name, email, phone, password, role, auth_provider, is_active, created_at, updated_at)
VALUES (
    'System Administrator',
    'admin@haripriya.com',
    '9999999999',
    '$2a$10$xqZ8Z8Z8Z8Z8Z8Z8Z8Z8ZuK5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y',
    'ADMIN',
    'INTERNAL',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- Note: The password hash above is a placeholder. 
-- You should generate a proper BCrypt hash for 'admin123' or your desired password.
-- You can use online BCrypt generators or run the application and use the registration endpoint.

COMMENT ON TABLE users IS 'Stores all users including internal staff (ADMIN, DOCTOR, RECEPTIONIST) and patients';
COMMENT ON COLUMN users.auth_provider IS 'Authentication method: INTERNAL for email/password, OTP for mobile OTP';
COMMENT ON COLUMN users.role IS 'User role: ADMIN, DOCTOR, RECEPTIONIST, or PATIENT';
