package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID orderId);
    List<Order> findByCustomerIdAndFilters(UUID customerId, UUID orderNumber, String startDate, String endDate);
}