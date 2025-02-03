package com.mwc.order.service.dataaccess.warehouse.repository;

import org.springframework.data.geo.Distance;
import com.mwc.order.service.dataaccess.warehouse.entity.WarehouseDocument;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseMongoRepository extends MongoRepository<WarehouseDocument, ObjectId> {

    @Query("{ 'id': { $eq: ?0 } }")
    Optional<WarehouseDocument> findByWarehouseId(String warehouseId);

    List<WarehouseDocument> findByLocationNear(Point location, Distance distance);

    @Aggregation(pipeline = {
            "{ $geoNear: { near: ?0, distanceField: 'distance', spherical: true } }"
    })
    List<WarehouseDocument> findNearestWarehouse(GeoJsonPoint location);


}
