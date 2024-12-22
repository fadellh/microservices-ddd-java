package com.mwc.order.service.domain.mapper;

import com.mwc.domain.valueobject.CustomerId;
import com.mwc.domain.valueobject.Money;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.order.service.domain.dto.create.OrderAddress;
import com.mwc.order.service.domain.dto.create.PreviewOrderCommand;
import com.mwc.order.service.domain.dto.create.PreviewOrderResponse;
import com.mwc.order.service.domain.entity.*;
import com.mwc.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Component
public class OrderDataMapper {

    public Order previewOrderCommandToOrder(PreviewOrderCommand previewOrderCommand) {
        return Order.builder()
                    .customerId(new CustomerId(previewOrderCommand.getCustomerId()))
                    .items(orderItemsToOrderItemEntities(previewOrderCommand.getItems()))
                    .deliveryAddress(orderAddressToStreetAddress(previewOrderCommand.getAddress()))
                    .build();
    }

private List<OrderItem> orderItemsToOrderItemEntities(
        @NotNull List<com.mwc.order.service.domain.dto.create.OrderItem> orderItems) {
    return orderItems.stream()
            .map(orderItem ->
                    OrderItem.builder()
                            .product(new Product(new ProductId(orderItem.getProductId())))
                            .price(new Money(orderItem.getPrice()))
                            .quantity(orderItem.getQuantity())
                            .subTotal(new Money(orderItem.getSubTotal()))
                            .build()).collect(Collectors.toList());
}

    public PreviewOrderResponse orderToPreviewOrderResponse(Order order, BigDecimal totalAmount, BigDecimal shippingCost, BigDecimal discount) {
        return PreviewOrderResponse.builder()
                .totalAmount(totalAmount)
                .shippingCost(shippingCost)
                .discount(discount)
                .items(order.getItems().stream()
                        .map(item -> PreviewOrderResponse.OrderItemResponse.builder()
                                .productName(item.getProduct().getName()) // Assuming Product has a getName() method
                                .productId(item.getProduct().getId().getValue())
                                .quantity(item.getQuantity())
                                .price(item.getPrice().getAmount())
                                .subTotal(item.getSubTotal().getAmount())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(
                UUID.randomUUID(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity()
        );
    }
}