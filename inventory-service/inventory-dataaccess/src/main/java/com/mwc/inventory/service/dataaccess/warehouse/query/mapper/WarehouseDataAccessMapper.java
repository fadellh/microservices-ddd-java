package com.mwc.inventory.service.dataaccess.warehouse.query.mapper;

import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.dataaccess.warehouse.query.entity.WarehouseEntity;
import com.mwc.inventory.service.domain.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WarehouseDataAccessMapper {

    public Warehouse warehouseEntityToWarehouse(Optional<WarehouseEntity> warehouseEntity) {
        WarehouseEntity entity = warehouseEntity.orElseThrow(() ->
                new IllegalArgumentException("WarehouseEntity must not be empty")
        );
        return Warehouse.builder()
                .id(new WarehouseId(entity.getId()))
                .name(entity.getName())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }

}
