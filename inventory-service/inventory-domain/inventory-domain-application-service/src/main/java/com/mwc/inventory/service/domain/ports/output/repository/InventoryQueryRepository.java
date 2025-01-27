package com.mwc.inventory.service.domain.ports.output.repository;

import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;

import java.util.List;

public interface InventoryQueryRepository {
    List<ProductItemResponse> getCatalogs();
}
