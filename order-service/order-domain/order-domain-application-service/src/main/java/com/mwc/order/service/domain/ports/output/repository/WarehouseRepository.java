package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Warehouse;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {
    Optional<Warehouse> findWarehouse(UUID warehouseId);
}