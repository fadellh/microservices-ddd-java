package com.mwc.order.service.dataaccess.order.query.mapper;

import com.mwc.domain.valueobject.*;
import com.mwc.order.service.dataaccess.order.query.entity.OrderDocument;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.entity.Product;
import com.mwc.order.service.domain.valueobject.StreetAddress;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderQueryDataAccessMapper {

    public OrderDocument orderToOrderDocument(Order order) {
        return OrderDocument.builder()
//                .id(new ObjectId(order.getId().getValue().toString()))
                .orderId(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .warehouseId(order.getWarehouseId().getValue())
                .totalAmount(order.getPrice().getAmount())
                .build();
    }

    public Order orderDocumentToOrder(OrderDocument document) {
        log.debug("Mapping OrderDocument: {}", document);

        return Order.builder()
                .orderId(new OrderId( Optional.ofNullable(document.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order ID is required"))))
                .customerId(new CustomerId(Optional.ofNullable(document.getCustomerId())
                        .orElseThrow(() -> new IllegalArgumentException("Customer ID is required"))))
                .price(new Money(Optional.ofNullable(document.getTotalAmount())
                        .orElse(BigDecimal.ZERO)))
                .warehouseId(new WarehouseId(Optional.ofNullable(document.getWarehouseId())
                        .orElseThrow(() -> new IllegalArgumentException("Warehouse ID is required"))))
                .orderStatus(document.getOrderStatus())
                .shippingCost(new Money(Optional.ofNullable(document.getShippingCost())
                        .orElse(BigDecimal.ZERO)))
                .deliveryAddress(new StreetAddress(null, document.getOrderAddress(), null, null))
                .items(Optional.ofNullable(document.getItems())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(item -> {
                            log.debug("Mapping OrderItem: {}", item);
                            return OrderItem.builder()
                                    .product(Product.builder().name(item.getName()).productId(new ProductId(item.getProductId())).build())
                                    .quantity(Optional.ofNullable(item.getQuantity()).orElse(0))
                                    .price(new Money(Optional.ofNullable(item.getPrice()).orElse(BigDecimal.ZERO)))
                                    .subTotal(new Money(Optional.ofNullable(item.getSubTotal()).orElse(BigDecimal.ZERO)))
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }

}
