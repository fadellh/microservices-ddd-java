package com.mwc.order.service.dataaccess.order.query.adapter;

import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.order.service.dataaccess.order.query.entity.OrderDocument;
import com.mwc.order.service.dataaccess.order.query.mapper.OrderQueryDataAccessMapper;
import com.mwc.order.service.dataaccess.order.query.repository.OrderMongoRepository;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.exception.OrderNotFoundException;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Qualifier("queryRepository")
public class OrderQueryRepositoryImpl implements OrderRepository {

    private final OrderMongoRepository orderMongoRepository;
    private final OrderQueryDataAccessMapper orderQueryDataAccessMapper;
    private final MongoTemplate mongoTemplate;

    public OrderQueryRepositoryImpl(OrderMongoRepository orderMongoRepository, OrderQueryDataAccessMapper orderQueryDataAccessMapper, MongoTemplate mongoTemplate) {
        this.orderMongoRepository = orderMongoRepository;
        this.orderQueryDataAccessMapper = orderQueryDataAccessMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Order save(Order order) {
        // Save domain entity to document and return domain entity
//        Optional<OrderDocument> orderDocument = orderMongoRepository.findByOrderId(order.getId().getValue().toString());
//
//        if (orderDocument.isPresent()) {
//            OrderDocument orderDoc = orderDocument.get();
//            orderDoc.setOrderStatus(order.getOrderStatus());
//            orderMongoRepository.save(orderDoc);
//        }else {
//            throw new OrderNotFoundException("Order not found for orderId: " + order.getId().getValue());
//        }

        updateOrderStatus(order.getId().getValue().toString(), order.getOrderStatus());

        return null;
    }


    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orderId").is(orderId));

        Update update = new Update().set("orderStatus", newStatus);

        OrderDocument updatedDoc = mongoTemplate.findAndModify(query, update, OrderDocument.class);

        if (updatedDoc == null) {
            throw new RuntimeException("Order not found for orderId: " + orderId);
        }

    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        // Find document by ID and convert to domain entity
        return orderMongoRepository.findByOrderId(orderId.toString())
                .map(orderQueryDataAccessMapper::orderDocumentToOrder);
    }


    @Override
    public List<Order> findByCustomerIdAndFilters(UUID customerId, UUID orderNumber, String startDate, String endDate) {
        return orderMongoRepository.findAll().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .filter(order -> orderNumber == null || order.getOrderId().equals(orderNumber))
//                .filter(order -> startDate == null || order.getOrderDate().compareTo(startDate) >= 0)
//                .filter(order -> endDate == null || order.getOrderDate().compareTo(endDate) <= 0)
                .map(orderQueryDataAccessMapper::orderDocumentToOrder)
                .collect(Collectors.toList());

//        return orderMongoRepository.findByCustomerIdAndFilters(customerId, orderNumber, startDate, endDate).stream()
//                .map(orderQueryDataAccessMapper::OrderDocumentToOrder)
//                .collect(Collectors.toList());

    }


}
