-- user-schema.sql
-- This file creates tables for the User Service in Postgres

CREATE TABLE IF NOT EXISTS admin_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    fullname VARCHAR(255),
    admin_role VARCHAR(50),
    active BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto-fill on creation
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_users (
    id UUID PRIMARY KEY,
    jwt_user_id  VARCHAR(100),
    email VARCHAR(255) NOT NULL UNIQUE,
    fullname VARCHAR(255),
    address TEXT,
    city VARCHAR(100),
    district VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto-fill on creation
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
