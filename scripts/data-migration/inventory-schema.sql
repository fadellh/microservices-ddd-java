-- inventory-schema.sql
-- This file creates tables for the Inventory Service (Postgres)

CREATE TABLE IF NOT EXISTS warehouses (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto-fill on creation
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);


CREATE TABLE IF NOT EXISTS inventory (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    total_quantity INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_items (
    id UUID PRIMARY KEY,
    inventory_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto-fill on creation
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_inventory_items_inventory
        FOREIGN KEY (inventory_id) REFERENCES inventory (id),
    CONSTRAINT fk_inventory_items_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);

CREATE TABLE IF NOT EXISTS stock_journal (
    id UUID PRIMARY KEY,
    inventory_item_id UUID NOT NULL,
    total_quantity_changed INT,
    quantity_changed INT,
    reason VARCHAR(50),
    type VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Auto-fill on creation
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_stock_journal_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items (id)
);
