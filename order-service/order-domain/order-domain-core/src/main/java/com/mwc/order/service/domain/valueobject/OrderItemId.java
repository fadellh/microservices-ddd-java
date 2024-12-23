package com.mwc.order.service.domain.valueobject;

import com.mwc.domain.valueobject.BaseId;


public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}