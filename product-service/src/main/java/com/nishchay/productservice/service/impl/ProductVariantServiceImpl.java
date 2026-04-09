package com.nishchay.productservice.service.impl;

import com.nishchay.productservice.dto.product_variant.ProductVariantResponseDto;
import com.nishchay.productservice.dto.product_variant.UpdateProductVariantRequestDto;
import com.nishchay.productservice.entity.ProductVariant;
import com.nishchay.productservice.repository.AttributeRepository;
import com.nishchay.productservice.repository.ProductRepository;
import com.nishchay.productservice.repository.ProductVariantRepository;
import com.nishchay.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    private final AttributeRepository attributeRepository;

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;
    @Override
    public ProductVariantResponseDto createProductVariant(String productId, Map<String, String> attributes, BigDecimal price, String sku, Integer initialStock, Integer reorderLevel) {
        return null;
    }

    @Override
    public List<ProductVariantResponseDto> getVariantsByProductId(String productId) {
        return List.of();
    }

    @Override
    public ProductVariantResponseDto updateProductVariant(Long variantId, UpdateProductVariantRequestDto updateDTO) {
        return null;
    }

    @Override
    public void deleteProductVariant(Long variantId) {

    }

    @Override
    public List<ProductVariant> getProductVariantByIds(Set<Long> variantIds) {
        return List.of();
    }

    @Override
    public ProductVariant getVariantById(Long variantId) {
        return null;
    }

    @Override
    public void saveProductVariant(ProductVariant productVariant) {

    }
}
