package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.CreateOrderResponse;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;

    private final OrderDataMapper orderDataMapper;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper, OrderRepository orderRepository) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
    }

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        // Persist Order
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());

        //Publish Message to Kafka
//        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);

        // Convert Order entity to CreateOrderResponse DTO
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }

}