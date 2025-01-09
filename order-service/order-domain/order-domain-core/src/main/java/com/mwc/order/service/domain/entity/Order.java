package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.*;
import com.mwc.order.service.domain.exception.OrderDomainException;
import com.mwc.order.service.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final StreetAddress deliveryAddress;
    private final WarehouseId warehouseId;
    private Money price;
    private Money shippingCost;
    private List<OrderItem> items;

    private OrderStatus orderStatus;
    private List<String> failureMessages;

    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        orderStatus = OrderStatus.AWAITING_PAYMENT;
        initializeOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    public BigDecimal calculateItemsTotalAmount() {
        return items.stream()
                .map(orderItem -> {
                    BigDecimal subTotal = orderItem.getSubTotal().getAmount();
                    return subTotal != null ? subTotal : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void initiateOrder() {
        orderStatus = OrderStatus.AWAITING_PAYMENT;
    }

    public void updateOrderStatus() {
        switch (orderStatus) {
            case AWAITING_PAYMENT:
                this.orderStatus = OrderStatus.REVIEW_PAYMENT;
                break;
            case REVIEW_PAYMENT:
                this.orderStatus = OrderStatus.APPROVED_PENDING;
                break;
            case APPROVED_PENDING:
                this.orderStatus = OrderStatus.APPROVED;
                break;
            case CANCEL_PENDING:
                this.orderStatus = OrderStatus.CANCELLED;
                break;
            default:
                throw new OrderDomainException("Order is not in correct state for operation!");
        }
    }

    public void initReviewPayment() {
        if (orderStatus != OrderStatus.AWAITING_PAYMENT) {
            throw new OrderDomainException("Order is not in correct state for initReviewPayment operation!");
        }
        orderStatus = OrderStatus.REVIEW_PAYMENT;
    }

    public void initApprove() {
        if (orderStatus != OrderStatus.REVIEW_PAYMENT) {
            throw new OrderDomainException("Order is not in correct state for initApprove operation!");
        }
        orderStatus = OrderStatus.APPROVED_PENDING;
    }

    public void approve() {
        if (orderStatus != OrderStatus.APPROVED_PENDING) {
            throw new OrderDomainException("Order is not in correct state for approve operation!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void cancelApprove(List<String> failureMessages) {
        if (orderStatus != OrderStatus.APPROVED_PENDING) {
            throw new OrderDomainException("Order is not in correct state for cancelApprove operation!");
        }
        orderStatus = OrderStatus.REVIEW_PAYMENT;
        updateFailureMessages(failureMessages);
    }

    public void pay() {
        updateOrderStatus();
    }

    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.AWAITING_PAYMENT) {
            throw new OrderDomainException("Order is not in correct state for initCancel operation!");
        }
        orderStatus = OrderStatus.CANCEL_PENDING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (!(orderStatus == OrderStatus.CANCEL_PENDING || orderStatus == OrderStatus.AWAITING_PAYMENT)) {
            throw new OrderDomainException("Order is not in correct state for cancel operation!");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }
        if (this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    private void validateInitialOrder() {
        if (orderStatus == null || getId() == null) {
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            throw new OrderDomainException("Total price: " + price.getAmount()
                    + " is not equal to Order items total: " + orderItemsTotal.getAmount() + "!");
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
                    " is not valid for product " + orderItem.getProduct().getId().getValue());
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem: items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        warehouseId = builder.warehouseId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        shippingCost = builder.shippingCost;
        items = builder.items;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public WarehouseId getWarehouseId() {
        return warehouseId;
    }


    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public Money getShippingCost() {
        return shippingCost;
    }

    public List<OrderItem> getItems() {
        return items;
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private WarehouseId warehouseId;
        private StreetAddress deliveryAddress;
        private Money price;
        private Money shippingCost;
        private List<OrderItem> items;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder warehouseId(WarehouseId val) {
            warehouseId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder shippingCost(Money val) {
            shippingCost = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }


        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

}