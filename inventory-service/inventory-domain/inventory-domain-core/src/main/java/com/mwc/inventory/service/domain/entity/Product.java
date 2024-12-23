package com.mwc.inventory.service.domain.entity;

import com.mwc.domain.entity.BaseEntity;
import com.mwc.domain.valueobject.Money;
import com.mwc.domain.valueobject.*;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId productId, String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public Product(ProductId productId) {
        super.setId(productId);
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }
}
