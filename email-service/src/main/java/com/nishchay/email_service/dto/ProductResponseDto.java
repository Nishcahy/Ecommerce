package com.nishchay.email_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
	
	 	private String id;
	    private String name;
	    private String description;
	    private String imageUrl;
	    private BigDecimal price;
	    private int version;

}
