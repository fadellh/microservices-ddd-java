-- Drop existing schema if it exists
DROP SCHEMA IF EXISTS "product" CASCADE;

-- Create new schema
CREATE SCHEMA "product";

-- Drop and create product table
DROP TABLE IF EXISTS "product".products CASCADE;
CREATE TABLE "product".products
(
    id uuid NOT NULL,
    name character varying NOT NULL,
    description character varying,
    price numeric(10,2) NOT NULL,
    CONSTRAINT product_pkey PRIMARY KEY (id)
);