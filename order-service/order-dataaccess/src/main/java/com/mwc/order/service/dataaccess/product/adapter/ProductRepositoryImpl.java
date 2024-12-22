package com.mwc.order.service.dataaccess.product.adapter;

import com.mwc.order.service.dataaccess.product.repository.ProductJpaRepository;
import com.mwc.order.service.dataaccess.product.mapper.ProductDataAccessMapper;
import com.mwc.order.service.domain.entity.Product;
import com.mwc.order.service.domain.ports.output.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductDataAccessMapper productDataAccessMapper;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository,
                                 ProductDataAccessMapper productDataAccessMapper) {
        this.productJpaRepository = productJpaRepository;
        this.productDataAccessMapper = productDataAccessMapper;
    }

    @Override
    public Optional<Product> findProduct(UUID productId) {
        return productJpaRepository.findById(productId).map(productDataAccessMapper::productEntityToProduct);
    }

    @Override
    public List<Product> findProductsByIds(List<UUID> productIds) {
        return productJpaRepository.findByIdIn(productIds).stream()
                .map(productDataAccessMapper::productEntityToProduct)
                .collect(Collectors.toList());
    }
}