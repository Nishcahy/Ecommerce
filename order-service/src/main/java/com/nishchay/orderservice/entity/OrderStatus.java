package com.nishchay.orderservice.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPING("Shipping"),
    DELIEVERD("Delieverd"),
    CANCELLED("Cancelled");

    private final String label;
}

