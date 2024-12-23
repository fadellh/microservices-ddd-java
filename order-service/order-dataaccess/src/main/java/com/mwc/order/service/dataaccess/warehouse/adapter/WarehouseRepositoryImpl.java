package com.mwc.order.service.dataaccess.warehouse.adapter;

import com.mwc.order.service.dataaccess.warehouse.repository.WarehouseJpaRepository;
import com.mwc.order.service.dataaccess.warehouse.mapper.WarehouseDataAccessMapper;
import com.mwc.order.service.domain.entity.Warehouse;
import com.mwc.order.service.domain.ports.output.repository.WarehouseRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseJpaRepository warehouseJpaRepository;
    private final WarehouseDataAccessMapper warehouseDataAccessMapper;

    public WarehouseRepositoryImpl(WarehouseJpaRepository warehouseJpaRepository,
                                   WarehouseDataAccessMapper warehouseDataAccessMapper) {
        this.warehouseJpaRepository = warehouseJpaRepository;
        this.warehouseDataAccessMapper = warehouseDataAccessMapper;
    }

    @Override
    public Optional<Warehouse> findWarehouse(UUID warehouseId) {
        return warehouseJpaRepository.findById(warehouseId).map(warehouseDataAccessMapper::warehouseEntityToWarehouse);
    }
}