package com.mwc.order.service.domain;

import com.mwc.domain.valueobject.OrderId;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.exception.OrderNotFoundException;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {
    private final OrderRepository orderRepository;
    private final OrderRepository orderQueryRepository;

    public OrderSagaHelper(
            @Qualifier("commandRepository") OrderRepository orderRepository,
            @Qualifier("queryRepository") OrderRepository orderQueryRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderQueryRepository = orderQueryRepository;
    }

    Order findOrder(UUID orderId) {
        Optional<Order> orderResponse = orderRepository.findById(new OrderId(orderId).getValue());
        if (orderResponse.isEmpty()) {
            log.error("Order with id: {} could not be found!", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " could not be found!");
        }
        return orderResponse.get();
    }

    void saveOrder(Order order) {
        // Update DB write model
        orderRepository.save(order);
        log.info("Successfully saved order with id: {}", order.getId().getValue());
        // Update DB read model
        orderQueryRepository.save(order);
        log.info("Successfully saved order with id: {} to query repository", order.getId().getValue());
    }



}
