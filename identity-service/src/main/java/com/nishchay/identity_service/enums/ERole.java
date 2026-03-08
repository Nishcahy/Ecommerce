package com.nishchay.identity_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ERole {
    ADMINISTRATOR("Administator"),
    EMPLOYEE("Employee"),
    CUSTOMER("Customer");

    private final String label;
}
