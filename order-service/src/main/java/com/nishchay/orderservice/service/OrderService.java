package com.nishchay.orderservice.service;

import com.nishchay.commonlib.dto.order.OrderDTO;
import com.nishchay.orderservice.dto.OrderRequestDto;
import com.nishchay.orderservice.dto.OrderResponseDto;
import com.nishchay.orderservice.dto.OrderResponseDtoWithOutOrderItem;

import java.util.List;

public interface OrderService {

    OrderDTO placeOrder(OrderRequestDto orderRequestDto,Long userId,String email);
    OrderResponseDto checkOrderStatusByOrderId(String orderId);
    OrderResponseDto updateOrderStatus(String orderId,int version);
    OrderDTO cancelOrder(String orderId,Long userId);
    List<OrderResponseDtoWithOutOrderItem> getAllOrders(Long userId,int page,int size);
}
