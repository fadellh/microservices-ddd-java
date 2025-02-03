package com.mwc.order.service.dataaccess.warehouse.adapter;

import com.mwc.order.service.dataaccess.warehouse.repository.WarehouseJpaRepository;
import com.mwc.order.service.dataaccess.warehouse.mapper.WarehouseDataAccessMapper;
import com.mwc.order.service.dataaccess.warehouse.repository.WarehouseMongoRepository;
import com.mwc.order.service.domain.entity.Warehouse;
import com.mwc.order.service.domain.ports.output.repository.WarehouseRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public List<Warehouse> findWarehousesNear(double latitude, double longitude, double radiusInKm) {
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radiusInKm, Metrics.KILOMETERS);
        return warehouseMongoRepository.findByLocationNear(point, distance)
                .stream()
                .map(warehouseDataAccessMapper::warehouseDocumentToWarehouse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Warehouse> findNearestWarehouse(double latitude, double longitude) {
        Point point = new Point(longitude, latitude);
        List<Warehouse> warehouses = warehouseMongoRepository.findNearestWarehouse(new GeoJsonPoint(point.getX(), point.getY()))
                .stream()
                .map(warehouseDataAccessMapper::warehouseDocumentToWarehouse)
                .collect(Collectors.toList());

        return warehouses;
    };



    }