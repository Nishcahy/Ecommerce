package com.nishchay.orderservice.dto.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeResponseDto {
    private Long id;
    private String name;
}