package com.nishchay.orderservice.service.state;

import com.nishchay.orderservice.entity.Order;
import com.nishchay.orderservice.entity.OrderStatus;

public class ShippingOrderState implements OrderState{

    @Override
    public void handleStateChange(Order order) {
        order.setStatus(OrderStatus.DELIEVERD.getLabel());
    }
}
