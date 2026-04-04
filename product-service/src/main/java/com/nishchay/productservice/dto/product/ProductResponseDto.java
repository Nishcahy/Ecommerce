package com.nishchay.productservice.dto.product;

import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.product_variant.ProductVariantResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private int version;
    private List<ProductVariantResponseDto> variants;
    private Set<CategoryResponseDto> categories;
}
