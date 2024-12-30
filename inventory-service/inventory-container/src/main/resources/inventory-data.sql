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
    ('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 100, NOW(), NULL, NULL),

    -- Inventory 2
    ('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 200, NOW(), NULL, NULL),

    -- Inventory 3
    ('33333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 300, NOW(), NULL, NULL);
