package com.mwc.inventory.service.domain;


import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;
import com.mwc.inventory.service.domain.mapper.InventoryDataMapper;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryQueryRepository;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InventoryQueryHandler {
    private final InventoryQueryRepository inventoryRepository;

    public InventoryQueryHandler(InventoryQueryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<ProductItemResponse> getCatalogs() {
        log.info("Get Catalogs");
        List<ProductItemResponse> data = inventoryRepository.getCatalogs();
        return data;
    }





}
