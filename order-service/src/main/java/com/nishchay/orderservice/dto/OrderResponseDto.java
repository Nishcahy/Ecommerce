package com.nishchay.orderservice.dto;

import com.nishchay.commonlib.dto.order.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private OrderDTO orderDTO;
    private PaymentDto paymentDto;

}
