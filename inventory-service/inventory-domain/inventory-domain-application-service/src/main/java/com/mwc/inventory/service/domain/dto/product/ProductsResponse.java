package com.mwc.inventory.service.domain.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductsResponse {
    final ProductItemResponse[] products;
}
