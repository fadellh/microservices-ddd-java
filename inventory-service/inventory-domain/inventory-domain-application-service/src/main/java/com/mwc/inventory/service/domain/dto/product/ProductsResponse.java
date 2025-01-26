package com.mwc.inventory.service.domain.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductsResponse {
    final List<ProductItemResponse> products;
}
