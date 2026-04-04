package com.nishchay.productservice.dto.product_variant;

import com.nishchay.productservice.dto.attribute_value.AttributeValueResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductVariantRequestDto {
    private Set<AttributeValueResponse> attributeValues;
    private BigDecimal price;
    private String sku;
    private Integer stockQuantity;
    private Integer reorderLevel;
}
