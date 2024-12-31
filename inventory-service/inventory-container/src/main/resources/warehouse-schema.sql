-- Create the schema if it doesn't already exist
CREATE SCHEMA IF NOT EXISTS warehouse;

-- Create the warehouses table
DROP TABLE IF EXISTS warehouse.warehouses CASCADE;
CREATE TABLE warehouse.warehouses (
    id UUID PRIMARY KEY,             -- Unique identifier for the warehouse
    name VARCHAR(255) NOT NULL,      -- Name of the warehouse
    latitude DOUBLE PRECISION NOT NULL,  -- Latitude of the warehouse
    longitude DOUBLE PRECISION NOT NULL  -- Longitude of the warehouse
);
