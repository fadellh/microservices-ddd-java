package com.mwc.order.service.dataaccess.order.query.adapter;

import com.mwc.order.service.dataaccess.order.query.mapper.OrderQueryDataAccessMapper;
import com.mwc.order.service.dataaccess.order.query.repository.OrderMongoRepository;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
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
        return orderMongoRepository.findById(orderId)
                .map(orderQueryDataAccessMapper::OrderDocumentToOrder);
    }


    @Override
    public List<Order> findByCustomerIdAndFilters(UUID customerId, UUID orderNumber, String startDate, String endDate) {
        return orderMongoRepository.findAll().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .filter(order -> orderNumber == null || order.getId().equals(orderNumber))
//                .filter(order -> startDate == null || order.getOrderDate().compareTo(startDate) >= 0)
//                .filter(order -> endDate == null || order.getOrderDate().compareTo(endDate) <= 0)
                .map(orderQueryDataAccessMapper::OrderDocumentToOrder)
                .collect(Collectors.toList());
    }


}
