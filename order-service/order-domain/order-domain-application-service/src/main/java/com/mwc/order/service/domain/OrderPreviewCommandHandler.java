package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.OrderItem;
import com.mwc.order.service.domain.dto.create.PreviewOrderCommand;
import com.mwc.order.service.domain.dto.create.PreviewOrderResponse;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderPreviewCommandHandler {

    private final OrderPreviewHelper orderPreviewHelper;

    private final OrderDataMapper orderDataMapper;


    public OrderPreviewCommandHandler(OrderPreviewHelper orderPreviewHelper, OrderDataMapper orderDataMapper) {
        this.orderPreviewHelper = orderPreviewHelper;
        this.orderDataMapper = orderDataMapper;
    }

    public PreviewOrderResponse previewOrder(PreviewOrderCommand previewOrderCommand) {
        Order order = orderDataMapper.previewOrderCommandToOrder(previewOrderCommand);
        // Perform necessary calculations and validations

        // validate user in User/Customer repository via helper
        orderPreviewHelper.checkCustomer(previewOrderCommand.getCustomerId());
        // validate product in Product repository via helper
        List<UUID> productIds = previewOrderCommand.getItems().stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toList());
        orderPreviewHelper.checkProduct(productIds);

        // calculate total amount
        BigDecimal totalAmount = order.calculateItemsTotalAmount();
        BigDecimal shippingCost = orderPreviewHelper.calculateShippingCost(order);
        BigDecimal discount = orderPreviewHelper.calculateDiscount(order);
        return orderDataMapper.orderToPreviewOrderResponse(order, totalAmount, shippingCost, discount);
    }
}