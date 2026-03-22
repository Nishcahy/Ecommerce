package com.nishchay.orderservice.dto.attribute_val;

import com.nishchay.orderservice.dto.attribute.AttributeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValueResponseDto {
    private AttributeResponseDto attribute;
    private String value;
}
