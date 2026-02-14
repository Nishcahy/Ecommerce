package com.nishchay.commonlib.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer stockQuantity;

}
