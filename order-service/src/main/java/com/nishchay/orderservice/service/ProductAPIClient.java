package com.nishchay.orderservice.service;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.orderservice.dto.product.ProductResponseDto;
import com.nishchay.orderservice.dto.product_variant.ProductVariantResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductAPIClient {

    @GetMapping("/api/v1/products")
    ResponseEntity<ApiResponce<List<ProductResponseDto>>> getProductsByIds(@RequestParam("ids") Set<String> productIds);

    @GetMapping("api/v1/products/variants")
    ResponseEntity<List<ProductVariantResponseDto>> getProductsByVariantIds(@RequestParam("variantIds") Set<Long> variantIds);

}
