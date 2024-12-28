package com.mwc.order.service.dataaccess.order.query.mapper;

import com.mwc.domain.valueobject.CustomerId;
import com.mwc.domain.valueobject.Money;
import com.mwc.domain.valueobject.OrderId;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.order.service.dataaccess.order.query.entity.OrderDocument;
import com.mwc.order.service.domain.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderQueryDataAccessMapper {

    public OrderDocument OrderToOrderDocument(Order order) {
        return OrderDocument.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .warehouseId(order.getWarehouseId().getValue())
                .totalAmount(order.getPrice().getAmount())
                .build();
    }

    public Order OrderDocumentToOrder(OrderDocument document) {
        return Order.builder()
                .orderId(new OrderId(document.getId()))
                .customerId(new CustomerId(document.getCustomerId()))
//                .deliveryAddress( document.getCustomerAddress())
                .warehouseId(new WarehouseId(document.getWarehouseId()))
                .price(new Money(document.getTotalAmount()))
                .build();
    }
}
