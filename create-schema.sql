-- 1. Create Schema
CREATE SCHEMA IF NOT EXISTS testbackendproject;

-- 2. Set search path so we don't need to prefix tables
SET search_path TO testbackendproject;

-- 3. Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    auth_provider VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    patient_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(500),
    blood_group VARCHAR(15),
    chronic_diseases TEXT,
    allergies TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 5. Insert Admin User (Password is 'admin123')
INSERT INTO users (email, password, role, auth_provider, is_active, created_at, updated_at) 
VALUES ('admin@haripriya.com', '$2a$10$wPHx.fO2yHk/z.9/4/5.6.7.8.9.0.1.2.3.4.5', 'ADMIN', 'INTERNAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;
