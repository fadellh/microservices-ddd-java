package com.mwc.order.service.dataaccess.warehouse.mapper;

import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.order.service.dataaccess.warehouse.entity.WarehouseEntity;
import com.mwc.order.service.domain.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseDataAccessMapper {

    public Warehouse warehouseEntityToWarehouse(WarehouseEntity warehouseEntity) {
        return Warehouse.builder()
                .warehouseId(new WarehouseId(warehouseEntity.getId()))
                .name(warehouseEntity.getName())
                .build();
    }

    public WarehouseEntity warehouseToWarehouseEntity(Warehouse warehouse) {
        return WarehouseEntity.builder()
                .id(warehouse.getId().getValue())
                .name(warehouse.getName())
                .build();
    }
}