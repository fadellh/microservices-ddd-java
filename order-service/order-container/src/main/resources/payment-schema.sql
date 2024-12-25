-- Drop existing schema if it exists
DROP SCHEMA IF EXISTS "payment" CASCADE;

-- Create new schema
CREATE SCHEMA "payment";

-- Create payment_status enum type
DROP TYPE IF EXISTS payment_status;
CREATE TYPE payment_status AS ENUM ('PENDING', 'PENDING_VERIFICATION', 'PAID', 'CANCELLED');

-- Create payment_method enum type
DROP TYPE IF EXISTS payment_method;
CREATE TYPE payment_method AS ENUM ('TRANSFER_UPLOAD');

-- Create payments table
DROP TABLE IF EXISTS "payment".payments CASCADE;
CREATE TABLE "payment".payments
(
    id uuid NOT NULL,
    order_id uuid NOT NULL,
    payment_method payment_method NOT NULL,
    payment_status payment_status NOT NULL,
    payment_date timestamp,
    payment_amount numeric(10,2) NOT NULL,
    payment_reference character varying,
    payment_proof_url character varying,
    CONSTRAINT payment_pkey PRIMARY KEY (id)
);