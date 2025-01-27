package com.mwc.order.service.dataaccess.product.mapper;

import com.mwc.domain.valueobject.Money;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.order.service.dataaccess.product.entity.ProductDocument;
import com.mwc.order.service.dataaccess.product.entity.ProductEntity;
import com.mwc.order.service.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductDataAccessMapper {

    public Product productEntityToProduct(ProductEntity productEntity) {
        return Product.builder()
                .productId(new ProductId(productEntity.getId()))
                .name(productEntity.getName())
                .price(new Money(productEntity.getPrice()))
                .build();
    }

    public ProductEntity productToProductEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId().getValue())
                .name(product.getName())
                .price(product.getPrice().getAmount())
                .build();
    }

    public Product productDocumentToProduct(ProductDocument productDocument) {
        return Product.builder()
                .productId(new ProductId(UUID.fromString(productDocument.getId())))
                .name(productDocument.getName())
                .price(new Money(productDocument.getPrice()))
                .build();
    }
}