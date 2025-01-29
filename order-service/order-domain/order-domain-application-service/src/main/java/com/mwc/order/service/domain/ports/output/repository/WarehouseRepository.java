package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Warehouse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {
    Optional<Warehouse> findWarehouse(UUID warehouseId);
    List<Warehouse> findWarehousesNear(double latitude, double longitude, double radiusInKm);
    List<Warehouse> findNearestWarehouse(double latitude, double longitude);
}