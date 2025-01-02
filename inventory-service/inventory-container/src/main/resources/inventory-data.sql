-- inventory-data.sql
-- Mengisi tabel inventory dengan data dummy

INSERT INTO inventory.inventory (
    id,
    product_id,
    total_quantity,
    created_at,
    updated_at,
    deleted_at
) VALUES
    -- Inventory 1
    ('11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 100, NOW(), NULL, NULL),

    -- Inventory 2
    ('22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 200, NOW(), NULL, NULL),

    -- Inventory 3
    ('33333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 300, NOW(), NULL, NULL);
