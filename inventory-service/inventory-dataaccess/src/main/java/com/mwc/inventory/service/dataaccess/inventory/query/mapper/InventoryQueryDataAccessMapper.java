package com.mwc.inventory.service.dataaccess.inventory.query.mapper;


import com.mwc.inventory.service.dataaccess.inventory.query.entity.CatalogDocument;
import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryQueryDataAccessMapper {

    public List<ProductItemResponse> catalogDocumentToProductItemResponse(List<CatalogDocument> catalogDocuments) {
        return catalogDocuments.stream().map(catalogDocument -> ProductItemResponse.builder()
                        .id(catalogDocument.getId())
                        .name(catalogDocument.getName())
                        .brand(catalogDocument.getBrand())
                        .price(catalogDocument.getPrice())
                        .image(catalogDocument.getImage())
                        .size(catalogDocument.getSize())
                        .availableColors(catalogDocument.getAvailableColors())
                        .maxQuantity(catalogDocument.getMaxQuantity())
                        .quantity(catalogDocument.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

}
