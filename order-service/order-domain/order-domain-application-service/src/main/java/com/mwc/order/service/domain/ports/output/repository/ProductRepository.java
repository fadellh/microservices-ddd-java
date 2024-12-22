package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> findProduct(UUID productId);

    List<Product> findProductsByIds(List<UUID> productIds);

}