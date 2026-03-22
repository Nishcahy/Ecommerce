package com.nishchay.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDtoWithOutOrderItem {
    private OrderWithOutOrderItems orderDTO;
    private PaymentDto paymentDto;
}
