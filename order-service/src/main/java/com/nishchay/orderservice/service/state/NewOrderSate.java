package com.nishchay.orderservice.service.state;

import com.nishchay.orderservice.entity.Order;
import com.nishchay.orderservice.entity.OrderStatus;

public class NewOrderSate implements OrderState{

    @Override
    public void handleStateChange(Order order) {
        order.setStatus(OrderStatus.PROCESSING.getLabel());
    }
}
