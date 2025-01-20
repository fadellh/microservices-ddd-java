package com.mwc.order.service.dataaccess.warehouse.adapter;

import com.mwc.order.service.dataaccess.warehouse.repository.WarehouseJpaRepository;
import com.mwc.order.service.dataaccess.warehouse.mapper.WarehouseDataAccessMapper;
import com.mwc.order.service.dataaccess.warehouse.repository.WarehouseMongoRepository;
import com.mwc.order.service.domain.entity.Warehouse;
import com.mwc.order.service.domain.ports.output.repository.WarehouseRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final WarehouseJpaRepository warehouseJpaRepository;
    private final WarehouseDataAccessMapper warehouseDataAccessMapper;
    private final WarehouseMongoRepository warehouseMongoRepository;

    public WarehouseRepositoryImpl(WarehouseJpaRepository warehouseJpaRepository,
                                   WarehouseDataAccessMapper warehouseDataAccessMapper, WarehouseMongoRepository warehouseMongoRepository) {
        this.warehouseJpaRepository = warehouseJpaRepository;
        this.warehouseDataAccessMapper = warehouseDataAccessMapper;
        this.warehouseMongoRepository = warehouseMongoRepository;
    }

    @Override
    public Optional<Warehouse> findWarehouse(UUID warehouseId) {
        return warehouseMongoRepository.findByWarehouseId(warehouseId.toString()).map(warehouseDataAccessMapper::warehouseDocumentToWarehouse);
    }
}