package com.nishchay.productservice.service;

import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.category.CreateCategoryRequestDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CreateCategoryRequestDto requestDto);
    CategoryResponseDto updateCategory(String id, CreateCategoryRequestDto requestDto);
    void deleteCategory(String id);
    CategoryResponseDto getCategoryById(String id);
    List<CategoryResponseDto> getAllCategories();
    List<CategoryResponseDto> getRootCategories();
}
