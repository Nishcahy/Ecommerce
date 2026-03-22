package com.nishchay.orderservice.service.state;

import com.nishchay.orderservice.entity.Order;

public interface OrderState {

    void handleStateChange(Order order);
}
