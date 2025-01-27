package com.mwc.order.service.dataaccess.order.command.mapper;

import com.mwc.domain.valueobject.*;
import com.mwc.order.service.dataaccess.order.command.entity.OrderAddressEntity;
import com.mwc.order.service.dataaccess.order.command.entity.OrderEntity;
import com.mwc.order.service.dataaccess.order.command.entity.OrderItemEntity;
import com.mwc.order.service.domain.dto.create.OrderAddress;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.entity.Product;
import com.mwc.order.service.domain.valueobject.OrderItemId;
import com.mwc.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderAddressEntity addressEntity = orderAddressToOrderAddressEntity(order.getDeliveryAddress());
        List<OrderItemEntity> itemEntities = orderItemsToOrderItemEntities(order.getItems());

        OrderEntity orderEntity =  OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .customerAddress(order.getDeliveryAddress().getStreet())
                .warehouseId(order.getWarehouseId().getValue())
                .totalAmount(order.calculateItemsTotalAmount())
                .shippingCost(new BigDecimal(10))
                .orderStatus(order.getOrderStatus())
                .failureMessages(String.join(Order.FAILURE_MESSAGE_DELIMITER,
                        order.getFailureMessages() != null ? order.getFailureMessages() : List.of()))
                .address(addressEntity)
                .items(itemEntities)
                .build();

        if (itemEntities != null) {
            itemEntities.forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        }

        if (addressEntity != null) {
            addressEntity.setOrder(orderEntity);
        }

        return orderEntity;

    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        List<String> failureMessages = orderEntity.getFailureMessages() == null
                ? Collections.emptyList()
                : Arrays.asList(orderEntity.getFailureMessages().split(","));

        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .deliveryAddress(orderAddressEntityToStreetAddress(orderEntity.getAddress()))
                .warehouseId(new WarehouseId(orderEntity.getWarehouseId()))
                .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .price(new Money(orderEntity.getTotalAmount()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(failureMessages)
                .build();
    }

    private OrderAddressEntity orderAddressToOrderAddressEntity(StreetAddress streetAddress) {
        return OrderAddressEntity.builder()
                .id(streetAddress.getId())
                .street(streetAddress.getStreet())
                .postalCode(streetAddress.getPostalCode())
                .city(streetAddress.getCity())
                .build();
    }

    private StreetAddress orderAddressEntityToStreetAddress(OrderAddressEntity orderAddressEntity) {
        return new StreetAddress(
                orderAddressEntity.getId(),
                orderAddressEntity.getStreet(),
                orderAddressEntity.getPostalCode(),
                orderAddressEntity.getCity()
        );
    }

    private OrderAddressEntity orderAddressToOrderAddressEntity(OrderAddress orderAddress) {
        return OrderAddressEntity.builder()
                .id(UUID.randomUUID())
                .street(orderAddress.getStreet())
                .postalCode(orderAddress.getPostalCode())
                .city(orderAddress.getCity())
                .latitude(orderAddress.getLatitude() != null ? Double.valueOf(orderAddress.getLatitude()) : null)
                .longitude(orderAddress.getLongitude() != null ? Double.valueOf(orderAddress.getLongitude()) : null)
                .build();
    }

    private OrderAddress orderAddressEntityToOrderAddress(OrderAddressEntity orderAddressEntity) {
        return new OrderAddress(
                orderAddressEntity.getStreet(),
                orderAddressEntity.getPostalCode(),
                orderAddressEntity.getCity(),
                orderAddressEntity.getLatitude() != null ? orderAddressEntity.getLatitude().toString() : null,
                orderAddressEntity.getLongitude() != null ? orderAddressEntity.getLongitude().toString() : null
        );
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .productId(orderItem.getProduct().getId().getValue())
                        .inventoryId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                .map(orderItemEntity -> OrderItem.builder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .product(new Product(new ProductId(orderItemEntity.getProductId())))
                        .price(new Money(orderItemEntity.getPrice()))
                        .quantity(orderItemEntity.getQuantity())
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }
}