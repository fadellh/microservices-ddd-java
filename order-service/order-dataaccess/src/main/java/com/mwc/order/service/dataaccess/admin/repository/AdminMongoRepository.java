package com.mwc.order.service.dataaccess.admin.repository;

import com.mwc.order.service.dataaccess.admin.entity.AdminDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AdminMongoRepository extends MongoRepository<AdminDocument, ObjectId> {

    @Query("{ 'id': { $eq: ?0 } }")
    Optional<AdminDocument> findByAdminId(String adminId);

}
