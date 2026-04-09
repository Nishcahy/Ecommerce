package com.nishchay.productservice.service.impl;

import com.nishchay.productservice.dto.product.CreateProductRequestDto;
import com.nishchay.productservice.dto.product.ProductResponseDto;
import com.nishchay.productservice.dto.product.UpdateProductRequestDto;
import com.nishchay.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {


    @Override
    public ProductResponseDto saveProduct(CreateProductRequestDto createProductRequestDto) {
        return null;
    }

    @Override
    public ProductResponseDto getProductById(String id) {
        return null;
    }

    @Override
    public Page<ProductResponseDto> getProductList(int page, int size) {
        return null;
    }

    @Override
    public ProductResponseDto updateProduct(String id, UpdateProductRequestDto productUpdateDto, int version) {
        return null;
    }

    @Override
    public void deleteProduct(String id) {

    }

    @Override
    public List<ProductResponseDto> getProductsByIds(Set<String> productIds) {
        return List.of();
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String name, String categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return null;
    }
}
