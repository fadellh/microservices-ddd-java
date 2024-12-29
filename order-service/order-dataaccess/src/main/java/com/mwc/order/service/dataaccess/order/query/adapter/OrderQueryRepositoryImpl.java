package com.mwc.order.service.dataaccess.order.query.adapter;

import com.mwc.order.service.dataaccess.order.query.mapper.OrderQueryDataAccessMapper;
import com.mwc.order.service.dataaccess.order.query.repository.OrderMongoRepository;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Qualifier("queryRepository")
public class OrderQueryRepositoryImpl implements OrderRepository {

    private final OrderMongoRepository orderMongoRepository;
    private final OrderQueryDataAccessMapper orderQueryDataAccessMapper;

    public OrderQueryRepositoryImpl(OrderMongoRepository orderMongoRepository, OrderQueryDataAccessMapper orderQueryDataAccessMapper) {
        this.orderMongoRepository = orderMongoRepository;
        this.orderQueryDataAccessMapper = orderQueryDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        // Find document by ID and convert to domain entity
//        List<Order> orders = orderMongoRepository.findAll().stream()
//                .filter(order -> order.getId().equals(orderId))
////                .filter(order -> order.getCustomerId().equals(customerId))
////                .filter(order -> startDate == null || order.getOrderDate().compareTo(startDate) >= 0)
////                .filter(order -> endDate == null || order.getOrderDate().compareTo(endDate) <= 0)
//                .map(orderQueryDataAccessMapper::OrderDocumentToOrder)
//                .toList();

//        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
//        ObjectId objectId = new ObjectId("6770e51dea3e0aa9e4497706");
        return orderMongoRepository.findByOrderId(orderId.toString())
                .map(orderQueryDataAccessMapper::orderDocumentToOrder);

//        return orderMongoRepository.findByOrderId(orderId)
//                .map(orderQueryDataAccessMapper::OrderDocumentToOrder);
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
