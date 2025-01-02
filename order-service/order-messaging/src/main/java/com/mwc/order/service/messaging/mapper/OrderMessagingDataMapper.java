package com.mwc.order.service.messaging.mapper;

import com.mwc.kafka.order.avro.model.OrderCreateAvroModel;
import com.mwc.kafka.order.avro.model.OrderStatus;
import com.mwc.kafka.order.avro.model.StockDecrementAvroModel;
import com.mwc.kafka.order.avro.model.StockDecrementResponseAvroModel;
import com.mwc.order.service.domain.dto.message.StockDecrementResponse;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
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

    public StockDecrementResponse stockDecrementResponseAvroModelToStockDecrementResponse(StockDecrementResponseAvroModel stockDecrementResponseAvroModel) {
        return StockDecrementResponse.builder()
                .orderId(stockDecrementResponseAvroModel.getOrderId())
                .sagaId(stockDecrementResponseAvroModel.getSagaId().toString())
                .inventoryId(stockDecrementResponseAvroModel.getInventoryId())
                .createdAt(stockDecrementResponseAvroModel.getCreatedAt())
                .failureMessage(stockDecrementResponseAvroModel.getFailureMessages())
                .build();
    }

    public StockDecrementAvroModel orderApprovedEventToStockDecrementAvroModel(OrderApprovedEvent domainEvent, OrderItem orderItem) {
        Order order = domainEvent.getOrder();
        return StockDecrementAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setOrderId(order.getId().getValue())
                .setWarehouseId(order.getWarehouseId().getValue())
                .setInventoryId(orderItem.getProduct().getId().getValue())
                .setProductId(orderItem.getProduct().getId().getValue())
                .setQuantity(orderItem.getQuantity())
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .build();
    }
}