package com.mwc.order.service.dataaccess.warehouse.repository;

import com.mwc.order.service.dataaccess.warehouse.entity.WarehouseDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface WarehouseMongoRepository extends MongoRepository<WarehouseDocument, ObjectId> {

    @Query("{ 'id': { $eq: ?0 } }")
    Optional<WarehouseDocument> findByWarehouseId(String warehouseId);
}
