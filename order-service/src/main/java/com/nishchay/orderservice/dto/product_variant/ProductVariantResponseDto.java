package com.nishchay.orderservice.dto.product_variant;

import com.nishchay.orderservice.dto.attribute_val.AttributeValueResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantResponseDto {
    private Long id;
    private Set<AttributeValueResponseDto> attributeValues;
    private BigDecimal price;
    private String sku;
    private Integer stockQuantity;
    private Integer reorderLevel;

}
