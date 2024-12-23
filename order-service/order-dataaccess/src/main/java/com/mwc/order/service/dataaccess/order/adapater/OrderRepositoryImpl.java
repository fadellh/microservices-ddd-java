package com.mwc.order.service.dataaccess.order.adapater;

import com.mwc.order.service.dataaccess.order.entity.OrderEntity;
import com.mwc.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.mwc.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        OrderEntity orderEntity = orderDataAccessMapper.orderToOrderEntity(order);
        if (orderEntity.getAddress() == null) {
            throw new IllegalArgumentException("Order address cannot be null");
        }
        if (orderEntity.getItems() == null || orderEntity.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }
        orderEntity = orderJpaRepository.save(orderEntity);
        return orderDataAccessMapper.orderEntityToOrder(orderEntity);
    }
}