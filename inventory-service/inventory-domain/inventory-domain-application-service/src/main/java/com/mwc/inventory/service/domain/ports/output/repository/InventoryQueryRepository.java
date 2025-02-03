package com.mwc.inventory.service.domain.ports.output.repository;

import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;
import com.mwc.inventory.service.domain.entity.Inventory;

import java.util.List;

public interface InventoryQueryRepository {
    List<ProductItemResponse> getCatalogs();
    void updateQuantityByInventoryIdAndWarehouseId(Inventory inventory);

}
