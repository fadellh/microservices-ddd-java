package com.mwc.order.service.dataaccess.order.query.repository;

import com.mwc.order.service.dataaccess.order.query.entity.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderMongoRepository extends MongoRepository<OrderDocument, UUID> {
    List<OrderDocument> findByCustomerId(UUID customerId);

    // Query with filtering for optional fields
    @Query("{ 'customerId': ?0, 'id': { $eq: ?1 }, 'orderDate': { $gte: ?2, $lte: ?3 } }")
    List<OrderDocument> findByCustomerIdAndFilters(UUID customerId, UUID orderNumber, String startDate, String endDate);
}
