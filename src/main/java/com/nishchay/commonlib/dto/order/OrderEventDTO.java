package com.nishchay.commonlib.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {
    private String message;
    private String status;
    private OrderDTO orderDTO;
    private String paymentMethod;
    private String email;
}
