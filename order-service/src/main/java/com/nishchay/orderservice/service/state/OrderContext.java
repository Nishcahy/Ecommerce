package com.nishchay.orderservice.service.state;

import com.nishchay.orderservice.entity.Order;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrderContext {
    private static final Logger LOGGER= LoggerFactory.getLogger(OrderContext.class);
    private OrderState orderState;

    public OrderContext(Order order){
        switch (order.getStatus()){
            case "Pending":
                this.orderState=new NewOrderSate();
                break;
            case "Proccesing":
                this.orderState=new ProcessingOrderState();
                break;
            case "Shipping":
                this.orderState=new ShippingOrderState();
                break;
            case "Delivered":
                this.orderState=new DeliveredOrderState();
                break;
            default:
                throw new IllegalStateException("unknow order state: "+order.getStatus());
        }
    }
    public void handleStateChange(Order order){
        orderState.handleStateChange(order);
    }

    public void setOrderState(OrderState orderState){
        this.orderState = orderState;
    }
}
