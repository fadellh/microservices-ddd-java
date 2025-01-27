package com.mwc.order.service.dataaccess.product.adapter;

import com.mwc.order.service.dataaccess.product.repository.ProductJpaRepository;
import com.mwc.order.service.dataaccess.product.mapper.ProductDataAccessMapper;
import com.mwc.order.service.dataaccess.product.repository.ProductMongoRepository;
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
    private final ProductMongoRepository productMongoRepository;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository,
                                 ProductDataAccessMapper productDataAccessMapper, ProductMongoRepository productMongoRepository) {
        this.productJpaRepository = productJpaRepository;
        this.productDataAccessMapper = productDataAccessMapper;
        this.productMongoRepository = productMongoRepository;
    }

    @Override
    public Optional<Product> findProduct(UUID productId) {
        return productJpaRepository.findById(productId).map(productDataAccessMapper::productEntityToProduct);
    }

    @Override
    public List<Product> findProductsByIds(List<UUID> productIds) {
        List<String> stringIds = productIds.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        return productMongoRepository.findByIdIn(stringIds).stream()
                .map(productDataAccessMapper::productDocumentToProduct)
                .collect(Collectors.toList());
    }
}