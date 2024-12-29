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
                        .totalPrice(order.getPrice().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    public RetrieveOrderDetailQueryResponse orderToRetrieveOrderDetailQueryResponse(Order order) {
        return RetrieveOrderDetailQueryResponse.builder()
                .orderNumber(order.getId().getValue())
                .orderStatus(order.getOrderStatus())
                .customerId(order.getCustomerId().getValue())
                .totalAmount(order.getPrice().getAmount())
                .items(order.getItems().stream()
                        .map(item -> OrderItemQuery.builder()
//                                .productName(item.getProduct().getName())
//                                .productId(item.getProduct().getId().getValue())
                                .quantity(item.getQuantity())
                                .price(item.getPrice().getAmount())
                                .subTotal(item.getSubTotal().getAmount())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}