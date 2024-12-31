package com.mwc.inventory.service.dataaccess.warehouse.query.adapter;

import com.mwc.inventory.service.dataaccess.warehouse.query.mapper.WarehouseDataAccessMapper;
import com.mwc.inventory.service.dataaccess.warehouse.query.repository.WarehouseJpaRepository;
import com.mwc.inventory.service.domain.entity.Warehouse;
import com.mwc.inventory.service.domain.ports.output.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WarehouseQueryRepository implements WarehouseRepository {

    private final WarehouseJpaRepository warehouseJpaRepository;
    private final WarehouseDataAccessMapper warehouseDataAccessMapper;

    public WarehouseQueryRepository(WarehouseJpaRepository warehouseJpaRepository, WarehouseDataAccessMapper warehouseDataAccessMapper) {
        this.warehouseJpaRepository = warehouseJpaRepository;
        this.warehouseDataAccessMapper = warehouseDataAccessMapper;
    }


    @Override
    public Optional<UUID> findNearestWarehouseWithinIds(Double latitude, Double longitude, List<UUID> warehouseIds) {
        return Optional.ofNullable(warehouseJpaRepository.findNearestWarehouseWithinIds(latitude, longitude, warehouseIds));
    }

    @Override
    public Optional<Warehouse> findById(UUID warehouseId) {
        return Optional.ofNullable(warehouseDataAccessMapper.warehouseEntityToWarehouse(warehouseJpaRepository.findById(warehouseId)));
    }
}
