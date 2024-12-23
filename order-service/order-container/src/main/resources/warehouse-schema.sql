CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE warehouse.warehouses (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);