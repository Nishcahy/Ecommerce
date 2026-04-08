package com.nishchay.email_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockResponseDto {
	
	private ProductResponseDto productResponseDto;
	private StockResponseDto stockResponseDto;
	

}
