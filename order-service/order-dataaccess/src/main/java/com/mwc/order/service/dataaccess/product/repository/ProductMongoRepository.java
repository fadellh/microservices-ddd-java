package com.mwc.order.service.dataaccess.product.repository;

import com.mwc.order.service.dataaccess.product.entity.ProductDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductMongoRepository extends MongoRepository<ProductDocument, ObjectId> {

    @Query("{ 'productId': { $eq: ?0 } }")
    Optional<ProductDocument> findByProductId(String productId);

    @Query("{ 'id': { $in: ?0 } }")
    List<ProductDocument> findByIdIn(List<UUID> productIds);

}
