-- Drop existing schema if it exists
DROP SCHEMA IF EXISTS "customer" CASCADE;

-- Create new schema
CREATE SCHEMA "customer";

-- Drop and create customer table
DROP TABLE IF EXISTS "customer".order_customer_m_view CASCADE;
CREATE TABLE "customer".order_customer_m_view
(
    id uuid NOT NULL,
    email character varying NOT NULL,
    fullname character varying NOT NULL,
    CONSTRAINT customer_pkey PRIMARY KEY (id)
);