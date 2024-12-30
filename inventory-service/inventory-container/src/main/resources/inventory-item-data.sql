-- inventory-item-data.sql
-- Mengisi tabel inventory_item dengan data dummy

INSERT INTO inventory.inventory_item (
    id,
    inventory_id,
    warehouse_id,
    quantity,
    created_at,
    updated_at,
    deleted_at
) VALUES
    -- Inventory Item 1 untuk Inventory 1
    ('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 50, NOW(), NULL, NULL),

    -- Inventory Item 2 untuk Inventory 1
    ('55555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 50, NOW(), NULL, NULL),

    -- Inventory Item 3 untuk Inventory 2
    ('66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 200, NOW(), NULL, NULL),

    -- Inventory Item 4 untuk Inventory 3
    ('77777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', '11111111-2222-3333-4444-555555555555', 150, NOW(), NULL, NULL),

    -- Inventory Item 5 untuk Inventory 3
    ('88888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333', '66666666-7777-8888-9999-aaaaaaaaaaaa', 150, NOW(), NULL, NULL);
