package com.mwc.inventory.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.InventoryId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.inventory.service.domain.exception.NegativeQuantityException;

public class Inventory extends AggregateRoot<InventoryId> {
    private InventoryId id;
    private ProductId productId;
    private int quantity;
    private String failureMessages;

    private Inventory(Builder builder) {
        this.id = builder.id;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.failureMessages = builder.failureMessages;
    }

    @Override
    public InventoryId getId() {
        return id;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new NegativeQuantityException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public static class Builder {
        private InventoryId id;
        private ProductId productId;
        private int quantity;
        private String failureMessages;

        public Builder id(InventoryId id) {
            this.id = id;
            return this;
        }

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder quantity(int quantity) {
            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }
            this.quantity = quantity;
            return this;
        }

        public Builder failureMessages(String failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public Inventory build() {
            return new Inventory(this);
        }
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getFailureMessages() {
        return failureMessages;
    }
}