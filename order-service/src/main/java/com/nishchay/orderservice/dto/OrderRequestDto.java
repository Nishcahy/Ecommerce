package com.nishchay.orderservice.dto;

import com.nishchay.commonlib.dto.order.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private String orderId;
    private String status;
    private String paymentMethod;
    private List<OrderItemDTO> orderItems;
}
