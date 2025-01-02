package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.CreateOrderResponse;
import com.mwc.order.service.domain.dto.create.UpdateOrderStatusCommand;
import com.mwc.order.service.domain.dto.create.UpdateOrderStatusResponse;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApprovedDecrementStockRequestMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;

    private final OrderDataMapper orderDataMapper;

    private final OrderCreatedMessagePublisher orderCreatedMessagePublisher;

    private final OrderApprovedDecrementStockRequestMessagePublisher orderApprovedDecrementStockRequestMessagePublisher;


    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper, OrderCreatedMessagePublisher orderCreatedMessagePublisher, OrderApprovedDecrementStockRequestMessagePublisher orderApprovedDecrementStockRequestMessagePublisher) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedMessagePublisher = orderCreatedMessagePublisher;
        this.orderApprovedDecrementStockRequestMessagePublisher = orderApprovedDecrementStockRequestMessagePublisher;
    }

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        // Persist Order
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());

        //Publish Message to Kafka
        orderCreatedMessagePublisher.publish(orderCreatedEvent);

        // Convert Order entity to CreateOrderResponse DTO
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }

    public CreatePaymentResponse createPayment(CreatePaymentCommand createPaymentCommand) {
        // Implementation for create payment
        return orderCreateHelper.uploadPayment(createPaymentCommand);
    }

    public UpdateOrderStatusResponse approveOrder(UpdateOrderStatusCommand updateOrderStatusCommand) {
        // Implementation for update order status
        OrderApprovedEvent orderApprovedEvent = orderCreateHelper.approveOrder(updateOrderStatusCommand);
        log.info("Order status is approved with id: {}", orderApprovedEvent.getOrder().getId().getValue());

        //Publish Message to Kafka
        orderApprovedDecrementStockRequestMessagePublisher.publish(orderApprovedEvent);

        return orderDataMapper.orderToOrderStatusResponse(orderApprovedEvent.getOrder());
    }


}