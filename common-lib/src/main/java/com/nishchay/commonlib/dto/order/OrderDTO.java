package com.nishchay.commonlib.dto.order;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private String orderId;
    private String status;
    private List<OrderItemDTO> orderItems;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
    private int version;
}
