package com.mwc.order.service.dataaccess.order.command.adapater;

import com.mwc.order.service.dataaccess.order.command.entity.OrderEntity;
import com.mwc.order.service.dataaccess.order.command.mapper.OrderDataAccessMapper;
import com.mwc.order.service.dataaccess.order.command.repository.OrderJpaRepository;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Qualifier("commandRepository")
public class OrderCommandRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderCommandRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
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

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId)
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public List<Order> findByCustomerIdAndFilters(UUID customerId, UUID orderNumber, String startDate, String endDate) {
        return List.of();
    }
}