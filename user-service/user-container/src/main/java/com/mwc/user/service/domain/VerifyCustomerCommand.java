package com.mwc.user.service.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
@Jacksonized
public class VerifyCustomerCommand {
    @NotNull
    private final String email;
}
