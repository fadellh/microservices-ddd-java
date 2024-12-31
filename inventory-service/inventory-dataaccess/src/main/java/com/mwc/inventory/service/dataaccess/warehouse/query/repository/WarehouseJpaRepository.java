package com.mwc.inventory.service.dataaccess.warehouse.query.repository;

import com.mwc.inventory.service.dataaccess.warehouse.query.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WarehouseJpaRepository extends JpaRepository<WarehouseEntity, UUID> {
    @Query(value = "SELECT id FROM warehouse.warehouses " +
            "WHERE id IN (:warehouseIds) " +
            "ORDER BY ST_DistanceSphere(ST_MakePoint(:longitude, :latitude), ST_MakePoint(longitude, latitude)) ASC " +
            "LIMIT 1",
            nativeQuery = true)
    UUID findNearestWarehouseWithinIds(@Param("latitude") Double latitude,
                                       @Param("longitude") Double longitude,
                                       @Param("warehouseIds") List<UUID> warehouseIds);


}
