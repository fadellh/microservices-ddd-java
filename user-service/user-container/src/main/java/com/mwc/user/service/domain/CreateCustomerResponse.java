package com.mwc.user.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateCustomerResponse {
    private final String id;
    private final String jwt_user_id;
    private final String fullname;
    private final String avatar = "https://cdn-icons-png.flaticon.com/512/147/147144.png";
    private final String banner = "https://salinaka-ecommerce.web.app/images/defaultBanner.accdc757f2c48d61f24c4fbcef2742fd.jpg";
    private final String email;
    private final String role = "USER";
    private final String address;
    private final Long dateJoined;
}