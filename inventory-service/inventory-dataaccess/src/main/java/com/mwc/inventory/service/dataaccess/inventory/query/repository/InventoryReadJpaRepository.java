package com.mwc.inventory.service.dataaccess.inventory.query.repository;

import com.mwc.inventory.service.dataaccess.inventory.query.entity.CatalogDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventoryReadJpaRepository extends MongoRepository<CatalogDocument, ObjectId> {


}
