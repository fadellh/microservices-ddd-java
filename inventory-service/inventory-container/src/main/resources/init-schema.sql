DROP SCHEMA IF EXISTS inventory CASCADE;

CREATE SCHEMA inventory;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS stock_journal_reason CASCADE;
CREATE TYPE stock_journal_reason AS ENUM (
    'AUTO_TRANSFER_IN',
    'AUTO_TRANSFER_OUT',
    'MANUAL_TRANSFER_IN',
    'MANUAL_TRANSFER_OUT',
    'ORDER'
);

DROP TYPE IF EXISTS stock_journal_type CASCADE;
CREATE TYPE stock_journal_type AS ENUM (
    'INCREASE',
    'DECREASE'
);

DROP TYPE IF EXISTS stock_journal_status CASCADE;
CREATE TYPE stock_journal_status AS ENUM (
    'REQUEST',
    'PROCESSED',
    'APPROVED',
    'REJECTED'
);

DROP TABLE IF EXISTS inventory.inventory CASCADE;
CREATE TABLE inventory.inventory (
    id uuid NOT NULL PRIMARY KEY,
    product_id UUID NOT NULL,
    total_quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS inventory.inventory_item CASCADE;
CREATE TABLE inventory.inventory_item (
    id uuid NOT NULL PRIMARY KEY,
    inventory_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_inventory FOREIGN KEY (inventory_id) REFERENCES inventory.inventory (id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS inventory.stock_journal CASCADE;
CREATE TABLE inventory.stock_journal (
    id uuid NOT NULL PRIMARY KEY,
    inventory_item_id UUID NOT NULL,
    total_quantity_changed INTEGER NOT NULL,
    quantity_changed INTEGER NOT NULL,
    reason stock_journal_reason NOT NULL,
    type stock_journal_type NOT NULL,
    status stock_journal_status NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_inventory_item FOREIGN KEY (inventory_item_id) REFERENCES inventory.inventory_item (id) ON DELETE CASCADE
);