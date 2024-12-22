package com.mwc.order.service.dataaccess.product.repository;

import com.mwc.order.service.dataaccess.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByIdIn(List<UUID> ids);
}
