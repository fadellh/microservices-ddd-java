package com.mwc.order.service.dataaccess.customer.repository;

import com.mwc.order.service.dataaccess.customer.entity.CustomerDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CustomerMongoRepository extends MongoRepository<CustomerDocument, ObjectId> {

    @Query("{ 'id': { $eq: ?0 } }")
    Optional<CustomerDocument> findByCustomerId(String customerId);
}
