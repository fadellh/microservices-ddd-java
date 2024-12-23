package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Order;

public interface OrderRepository {
    Order save(Order order);
}