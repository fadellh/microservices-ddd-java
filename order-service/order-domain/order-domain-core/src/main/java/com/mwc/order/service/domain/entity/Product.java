package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.BaseEntity;
import com.mwc.domain.valueobject.*;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;


    public Product(ProductId productId) {
        super.setId(productId);
    }
    private Product(Builder builder) {
        super.setId(builder.productId);
        this.name = builder.name;
        this.price = builder.price;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public static final class Builder {
        private ProductId productId;
        private String name;
        private Money price;

        private Builder() {
        }

        public Builder productId(ProductId val) {
            productId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }

}
