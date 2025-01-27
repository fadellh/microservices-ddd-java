package com.mwc.inventory.service.dataaccess.inventory.query.adapter;


import com.mwc.inventory.service.dataaccess.inventory.query.mapper.InventoryQueryDataAccessMapper;
import com.mwc.inventory.service.dataaccess.inventory.query.repository.InventoryReadJpaRepository;
import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryQueryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("inventoryQueryRepository")
public class InventoryQueryRepositoryImpl implements InventoryQueryRepository {

    private final InventoryReadJpaRepository  inventoryReadJpaRepository;
    private final InventoryQueryDataAccessMapper inventoryQueryDataAccessMapper;

    public InventoryQueryRepositoryImpl(InventoryReadJpaRepository inventoryReadJpaRepository, InventoryQueryDataAccessMapper inventoryQueryDataAccessMapper) {
        this.inventoryReadJpaRepository = inventoryReadJpaRepository;
        this.inventoryQueryDataAccessMapper = inventoryQueryDataAccessMapper;
    }

    @Override
    public List<ProductItemResponse> getCatalogs() {
        return inventoryQueryDataAccessMapper.catalogDocumentToProductItemResponse(inventoryReadJpaRepository.findAll());
    }

}
