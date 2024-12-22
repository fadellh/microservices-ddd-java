package com.mwc.order.service.dataaccess.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products", schema = "product")
@Entity
public class ProductEntity {

    @Id
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;

}