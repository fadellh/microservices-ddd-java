package com.mwc.inventory.service.domain.ports.output.repository;

import com.mwc.inventory.service.domain.entity.Warehouse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {
   Optional<UUID> findNearestWarehouseWithinIds(Double latitude, Double longitude ,List<UUID> warehouseIds);
   Optional<Warehouse> findById(UUID warehouseId);
}
