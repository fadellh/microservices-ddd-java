-- Drop existing schema if it exists
DROP SCHEMA IF EXISTS "admin" CASCADE;

-- Create new schema
CREATE SCHEMA "admin";

DROP TYPE IF EXISTS admin_role;
CREATE TYPE admin_role AS ENUM ('WAREHOUSE_ADMIN', 'SUPER_ADMIN');

-- Drop and create customer table
DROP TABLE IF EXISTS "admin".order_admin_m_view CASCADE;
CREATE TABLE "admin".order_admin_m_view
(
    id uuid NOT NULL,
    email character varying NOT NULL,
    fullname character varying NOT NULL,
    admin_role admin_role NOT NULL,
    active boolean NOT NULL DEFAULT true,
    CONSTRAINT customer_pkey PRIMARY KEY (id)
);