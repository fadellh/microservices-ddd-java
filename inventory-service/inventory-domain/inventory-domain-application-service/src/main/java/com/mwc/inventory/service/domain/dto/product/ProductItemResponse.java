package com.mwc.inventory.service.domain.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ProductItemResponse {
    final UUID id;
    final String name;
    final String brand;
    final BigDecimal price;
    final String image;
    final String size;
    final List<String> availableColors;
    final int maxQuantity;
    final int quantity;
}
