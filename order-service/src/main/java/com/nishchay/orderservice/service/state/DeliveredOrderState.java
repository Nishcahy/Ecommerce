package com.nishchay.orderservice.service.state;

import com.nishchay.orderservice.entity.Order;

public class DeliveredOrderState implements OrderState{
    @Override
    public void handleStateChange(Order order) {
        throw new IllegalStateException("Order has already been delivered and cannot be updated.....");
    }
}
