package com.mwc.order.service.messaging.mapper;

import com.mwc.kafka.order.avro.model.OrderCreateAvroModel;
import com.mwc.kafka.order.avro.model.OrderStatus;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public OrderCreateAvroModel orderCreatedEventToOrderCreateAvroModel(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return OrderCreateAvroModel.newBuilder()
                .setId(order.getId().getValue())
                .setSagaId(UUID.randomUUID())
                .setCustomerId(order.getCustomerId().getValue())
                .setWarehouseId(order.getWarehouseId().getValue())
                .setOrderStatus(OrderStatus.AWAITING_PAYMENT)
                .setTotalAmount(order.getPrice().getAmount())
                .setShippingCost(order.getPrice().getAmount())
                .setItems(order.getItems().stream().map(item ->
                        com.mwc.kafka.order.avro.model.OrderItem.newBuilder()
                                .setId(item.getId().getValue())
                                .setProductId(item.getId().getValue().toString())
                                .setQuantity(item.getQuantity())
                                .setPrice(item.getPrice().getAmount())
                                .setSubTotal(item.getSubTotal().getAmount())
                                .build()).collect(Collectors.toList()))
                .setCustomerAddress(order.getDeliveryAddress().getStreet())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .build();
    }
}