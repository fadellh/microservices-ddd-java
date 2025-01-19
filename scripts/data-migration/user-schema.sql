-- user-schema.sql
-- This file creates tables for the User Service in Postgres

CREATE TABLE IF NOT EXISTS admin_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    fullname VARCHAR(255),
    admin_role VARCHAR(50),
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS customer_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    fullname VARCHAR(255)
);
