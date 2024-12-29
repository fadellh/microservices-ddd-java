package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    private String name;

    public Customer() {
    }

    private Customer(Builder builder) {
        super.setId(builder.customerId);
        name = builder.name;
    }
    public static Customer.Builder builder() {
        return new Customer.Builder();
    }


    public static final class Builder {
        private CustomerId customerId;
        private String name;

        private Builder() {
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}