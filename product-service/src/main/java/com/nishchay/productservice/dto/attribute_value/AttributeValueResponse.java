package com.nishchay.productservice.dto.attribute_value;

import com.nishchay.productservice.dto.attribute.AttributezResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValueResponse {
    private AttributezResponseDto attribute;
    private String value;
}
