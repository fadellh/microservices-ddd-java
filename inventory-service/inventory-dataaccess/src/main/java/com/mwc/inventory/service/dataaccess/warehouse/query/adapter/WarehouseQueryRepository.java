package com.mwc.inventory.service.dataaccess.warehouse.query.adapter;

import com.mwc.inventory.service.dataaccess.warehouse.query.repository.WarehouseJpaRepository;
import com.mwc.inventory.service.domain.ports.output.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WarehouseQueryRepository implements WarehouseRepository {

    private final WarehouseJpaRepository warehouseJpaRepository;

    public WarehouseQueryRepository(WarehouseJpaRepository warehouseJpaRepository) {
        this.warehouseJpaRepository = warehouseJpaRepository;
    }


    @Override
    public Optional<UUID> findNearestWarehouseWithinIds(Double latitude, Double longitude, List<UUID> warehouseIds) {
        return Optional.ofNullable(warehouseJpaRepository.findNearestWarehouseWithinIds(latitude, longitude, warehouseIds));
    }
}
