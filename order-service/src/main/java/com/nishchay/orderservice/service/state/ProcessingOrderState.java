package com.nishchay.orderservice.service.state;

import com.nishchay.commonlib.dto.order.OrderDTO;
import com.nishchay.orderservice.entity.Order;
import com.nishchay.orderservice.entity.OrderStatus;

public class ProcessingOrderState implements OrderState{

    @Override
    public void handleStateChange(Order order) {
        order.setStatus(OrderStatus.SHIPPING.getLabel());
    }
}
