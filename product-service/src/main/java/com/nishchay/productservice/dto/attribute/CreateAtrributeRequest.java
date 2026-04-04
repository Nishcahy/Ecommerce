package com.nishchay.productservice.dto.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAtrributeRequest {
    private String name;
    private String dataType;
}
