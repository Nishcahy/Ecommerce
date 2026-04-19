package com.nishchay.productservice.dto.product;

import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.product_variant.ProductVariantResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ProductCacheDto implements Serializable {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private int version;
    private List<ProductVariantResponseDto> variants;
    private Set<CategoryResponseDto> categories;
}
