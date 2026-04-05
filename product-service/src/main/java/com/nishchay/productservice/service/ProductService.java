package com.nishchay.productservice.service;

import com.nishchay.productservice.dto.product.CreateProductRequestDto;
import com.nishchay.productservice.dto.product.ProductResponseDto;
import com.nishchay.productservice.dto.product.UpdateProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductResponseDto saveProduct(CreateProductRequestDto createProductRequestDto);
    ProductResponseDto getProductById(String id);
    Page<ProductResponseDto> getProductList(int page, int size);
    ProductResponseDto updateProduct(String id, UpdateProductRequestDto productUpdateDto, int version);
    void deleteProduct(String id);
    List<ProductResponseDto> getProductsByIds(Set<String> productIds);
    Page<ProductResponseDto> searchProducts(String name, String categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
