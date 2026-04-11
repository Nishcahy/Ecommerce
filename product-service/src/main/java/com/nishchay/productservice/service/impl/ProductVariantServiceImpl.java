package com.nishchay.productservice.service.impl;

import com.nishchay.productservice.dto.attribute_value.AttributeValueResponse;
import com.nishchay.productservice.dto.product_variant.ProductVariantResponseDto;
import com.nishchay.productservice.dto.product_variant.UpdateProductVariantRequestDto;
import com.nishchay.productservice.entity.Attribute;
import com.nishchay.productservice.entity.AttributeValue;
import com.nishchay.productservice.entity.Product;
import com.nishchay.productservice.entity.ProductVariant;
import com.nishchay.productservice.exception.ProductException;
import com.nishchay.productservice.redis.ProductRedis;
import com.nishchay.productservice.repository.AttributeRepository;
import com.nishchay.productservice.repository.ProductRepository;
import com.nishchay.productservice.repository.ProductVariantRepository;
import com.nishchay.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ProductRedis productRedis;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public ProductVariantResponseDto createProductVariant(String productId, Map<String, String> attributes, BigDecimal price, String sku, Integer initialStock, Integer reorderLevel) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new RuntimeException("Producr not fpound woith id "+productId));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setPrice(price);
        variant.setSku(sku);
        variant.setStockQuantity(initialStock);
        variant.setReorderLevel(reorderLevel);

        for(Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();

            Attribute attribute = attributeRepository.findByName(attributeName)
                    .orElseThrow(() -> new RuntimeException("Attribute not found: " + attributeName));
            AttributeValue attributeValue=new AttributeValue();
            attributeValue.setAttribute(attribute);
            attributeValue.setValue(entry.getValue());
            attributeValue.setProductVariant(variant);


            variant.getAttributeValues().add(attributeValue);
        }
        ProductVariant savedVariant = productVariantRepository.save(variant);
        product.getVariants().add(savedVariant);
        productRedis.save(product);
        return modelMapper.map(savedVariant, ProductVariantResponseDto.class);
    }

    @Override
    public List<ProductVariantResponseDto> getVariantsByProductId(String productId) {
        return productVariantRepository.findByProductId(productId).stream()
                .map(variant -> modelMapper.map(variant, ProductVariantResponseDto.class))
                .toList();
    }

    @Transactional
    @Override
    public ProductVariantResponseDto updateProductVariant(Long variantId, UpdateProductVariantRequestDto updateDTO) {
        ProductVariant productVariant=productVariantRepository.findById(variantId)
                .orElseThrow(()->new ProductException("Product variant not found with id "+variantId, HttpStatus.NOT_FOUND));
        if(updateDTO.getSku()!=null){
            productVariant.setSku(updateDTO.getSku());
        }
        if(updateDTO.getPrice()!=null){
            productVariant.setPrice(updateDTO.getPrice());
        }
        if(updateDTO.getStockQuantity()!=null) {
            productVariant.setStockQuantity(updateDTO.getStockQuantity());
        }
        if(updateDTO.getReorderLevel()!=null) {
            productVariant.setReorderLevel(updateDTO.getReorderLevel());
        }
        if(updateDTO.getAttributeValues()!=null && !updateDTO.getAttributeValues().isEmpty()){
            productVariant.getAttributeValues().clear();

            for(AttributeValueResponse attributeValueResponse : updateDTO.getAttributeValues()) {
                Attribute attribute = attributeRepository.findByName(attributeValueResponse.getAttribute().getName())
                        .orElseThrow(() -> new RuntimeException("Attribute not found: " + attributeValueResponse.getAttribute().getName()));
                AttributeValue attributeValue=new AttributeValue();
                attributeValue.setAttribute(attribute);
                attributeValue.setValue(attributeValueResponse.getValue());
                attributeValue.setProductVariant(productVariant);
                productVariant.getAttributeValues().add(attributeValue);
            }
        }
        ProductVariant updatedProductVariant=productVariantRepository.save(productVariant);

        Product productAfterUpadte=productVariant.getProduct();
        productRedis.save(productAfterUpadte);
        return modelMapper.map(updatedProductVariant,ProductVariantResponseDto.class);
    }

    @Transactional
    @Override
    public void deleteProductVariant(Long variantId) {
        ProductVariant productVariant=productVariantRepository.findById(variantId)
                .orElseThrow(()->new ProductException("Product variant not found with id "+variantId, HttpStatus.NOT_FOUND));
        Product product=productVariant.getProduct();
        productVariantRepository.delete(productVariant);
        productRedis.save(product);
    }

    @Override
    public List<ProductVariant> getProductVariantByIds(Set<Long> variantIds) {
        return productVariantRepository.findAllByIdIn(variantIds);
    }

    @Override
    public ProductVariant getVariantById(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new ProductException("Product variant not found with ID: " + variantId, HttpStatus.NOT_FOUND));
        return variant;
    }

    @Override
    public void saveProductVariant(ProductVariant productVariant) {
        productVariantRepository.save(productVariant);
    }
}
