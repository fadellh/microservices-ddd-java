-- order-schema.sql
-- This file creates the tables for the Order Service in Postgres
-- including relationships (foreign keys).

CREATE TABLE IF NOT EXISTS warehouses (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    customer_address TEXT,
    warehouse_id UUID NOT NULL,
    total_amount NUMERIC,
    shipping_cost NUMERIC,
    order_status VARCHAR(50),
    failure_messages TEXT
    -- CONSTRAINT fk_orders_warehouse
    --     FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);

CREATE TABLE IF NOT EXISTS order_address (
    id UUID PRIMARY KEY,
    city VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    postal_code VARCHAR(20),
    street VARCHAR(255),
    order_id UUID NOT NULL,
    CONSTRAINT fk_order_address_order
        FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    price NUMERIC,
    quantity INT,
    sub_total NUMERIC,
    PRIMARY KEY (id, order_id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders (id)
);
