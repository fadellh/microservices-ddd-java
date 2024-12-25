package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.*;

public class Payment extends AggregateRoot<PaymentId> {
    private final OrderId orderId;
    private final Money amount;
    private final PaymentStatus status;
    private final PaymentMethod paymentMethod;
    private final String proofUrl;

    private Payment(Builder builder) {
        super.setId(builder.paymentId);
        orderId = builder.orderId;
        amount = builder.amount;
        status = builder.status;
        paymentMethod = builder.paymentMethod;
        proofUrl = builder.proofUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getProofUrl(){
         return  proofUrl;
    }


    public static final class Builder {
        private PaymentId paymentId;
        private OrderId orderId;
        private Money amount;
        private PaymentStatus status;
        private PaymentMethod paymentMethod;
        private String proofUrl;

        private Builder() {
        }

        public Builder paymentId(PaymentId paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder proofUrl(String proofUrl) {
            this.proofUrl = proofUrl;
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
