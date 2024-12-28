package com.mwc.order.service.dataaccess.order.query.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddressDocument {
    private String city;
    private Double latitude;
    private Double longitude;
    private String postalCode;
    private String street;
}
