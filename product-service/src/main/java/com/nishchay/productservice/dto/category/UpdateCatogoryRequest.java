package com.nishchay.productservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCatogoryRequest {
    private String id;
    private String name;
    private String parentId;
}
