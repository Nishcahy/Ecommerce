package com.nishchay.email_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.email_service.dto.ProductStockResponseDto;

@FeignClient(name="PRODUCT-SERVICE")
public interface ProductApiClient {
	
	@GetMapping("api/v1/products/{id}")
	ResponseEntity<ApiResponce<ProductStockResponseDto>> getProductById(@RequestParam("id") String id);
}
