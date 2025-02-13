package com.mwc.order.service.domain.mapper;

import com.mwc.domain.valueobject.*;
import com.mwc.order.service.domain.dto.create.*;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.dto.retrieve.order.*;
import com.mwc.order.service.domain.entity.*;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
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

    public PreviewOrderResponse orderToPreviewOrderResponse(Order order, BigDecimal totalAmount, BigDecimal shippingCost, BigDecimal discount, List<Warehouse> nearestWarehouse) {
        return PreviewOrderResponse.builder()
                .totalAmount(totalAmount)
                .shippingCost(shippingCost)
                .discount(discount)
                .warehouseName(nearestWarehouse.get(0).getName())
                .warehouseId(nearestWarehouse.get(0).getId().getValue())
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

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .warehouseId(new WarehouseId(createOrderCommand.getWarehouseId()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
                .orderId(order.getId().getValue())
                .orderStatus(order.getOrderStatus())
                .message("Order created successfully")
                .build();
    }


    public CreatePaymentResponse paymentToCreatePaymentResponse(Payment payment) {
       return CreatePaymentResponse.builder()
               .paymentProofUrl(payment.getProofUrl())
               .paymentStatus(payment.getStatus())
               .message("Upload Success")
               .build();
    }


    public Payment createPaymentCommandToPayment(CreatePaymentCommand createPaymentCommand) {
        return Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .orderId(new OrderId(createPaymentCommand.getOrderId()))
                .build();
    }

    public UpdateOrderStatusResponse orderToOrderStatusResponse(Order order) {
        return UpdateOrderStatusResponse.builder()
                .orderStatus(order.getOrderStatus())
                .message("Order status updated successfully")
                .build();
    }

    public Order updateOrderStatusCommandToOrder(UpdateOrderStatusCommand updateOrderStatusCommand, Order order) {
        return Order.builder()
                .orderId(new OrderId(updateOrderStatusCommand.getOrderId()))
                .orderStatus(updateOrderStatusCommand.getOrderStatus())
                .customerId(order.getCustomerId())
                .warehouseId(order.getWarehouseId())
                .items(order.getItems())
                .deliveryAddress(order.getDeliveryAddress())
                .price(order.getPrice())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    public List<RetrieveOrderQueryResponse> orderListToRetrieveOrderQueryResponse(List<Order> orders) {
        return orders.stream()
                .map(order -> RetrieveOrderQueryResponse.builder()
                        .orderNumber(order.getId().getValue())
                        .orderStatus(order.getOrderStatus())
                        .totalPrice(order.calculateItemsTotalAmount())
                        .build())
                .collect(Collectors.toList());
    }

    public RetrieveOrderDetailQueryResponse orderToRetrieveOrderDetailQueryResponse(Order order) {
        return RetrieveOrderDetailQueryResponse.builder()
                .orderNumber(order.getId().getValue())
                .orderStatus(order.getOrderStatus())
                .customerId(order.getCustomerId().getValue())
                .customerAddress(order.getDeliveryAddress().getStreet())
                .totalAmount(order.calculateItemsTotalAmount())
                .shippingCost(order.getShippingCost().getAmount())
                .items(order.getItems().stream()
                        .map(item -> OrderItemQuery.builder()
                                .name(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice().getAmount())
                                .subTotal(item.getSubTotal().getAmount())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public List<OrderItem> previewOrderCommandToOrderItems(@NotNull List<com.mwc.order.service.domain.dto.create.OrderItem> items) {
        return items.stream()
                .map(item -> OrderItem.builder()
                        .product(new Product(new ProductId(item.getProductId())))
                        .price(new Money(item.getPrice()))
                        .quantity(item.getQuantity())
                        .subTotal(new Money(item.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }

    // Map products to OrderItem
    public List<OrderItem> productsToOrderItems(List<Product> products, PreviewOrderCommand previewOrderCommand) {

        List<com.mwc.order.service.domain.dto.create.OrderItem> items = previewOrderCommand.getItems();
        //quantity using index
        return products.stream()
                .map(product -> OrderItem.builder()
                        .product(product)
                        .price(product.getPrice())
                        .quantity(items.get(products.indexOf(product)).getQuantity())
                        .subTotal(product.getPrice().multiply(items.get(products.indexOf(product)).getQuantity()))
                        .build())
                .collect(Collectors.toList());
    }

}