package com.mwc.order.service.domain.dto.retrieve.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderAddressQuery {
    private final String city;
    private final String postalCode;
    private final String street;
}