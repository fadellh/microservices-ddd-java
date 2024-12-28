package com.mwc.order.service.dataaccess.order.command.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_address")
public class OrderAddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String city;
    private Double latitude;
    private Double longitude;
    private String postalCode;
    private String street;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}