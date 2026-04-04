package com.nishchay.productservice.dto;

import com.nishchay.productservice.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockResponse {
    private ProductResponseDto product;
}
