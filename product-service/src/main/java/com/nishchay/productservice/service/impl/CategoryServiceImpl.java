package com.nishchay.productservice.service.impl;

import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.category.CreateCategoryRequestDto;
import com.nishchay.productservice.entity.Category;
import com.nishchay.productservice.repository.CategoryRepository;
import com.nishchay.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryResponseDto createCategory(CreateCategoryRequestDto requestDto) {
        Category category = new Category();
        category.setId(UUID.randomUUID().toString());
        category.setName(requestDto.getName());
        if(requestDto.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parentCategory);
        }
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Transactional
    @Override
    public CategoryResponseDto updateCategory(String id, CreateCategoryRequestDto requestDto) {
        Category category=categoryRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Category not found"));

        category.setName(requestDto.getName());
        if(requestDto.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parentCategory);
        }else {
            category.setParent(null);
        }
        return convertToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(String id) {
            Category category=categoryRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("Category not found"));
            categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDto getCategoryById(String id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(()->new RuntimeException("Category not found"));
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::convertToDto)
                .toList();
    }

    private CategoryResponseDto convertToDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if(category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }else{
            dto.setParentId(null);
        }

        if(category.getChildren()!=null && !category.getChildren().isEmpty()){
            List<CategoryResponseDto> categoryResponseDtos= category.getChildren().stream()
                    .map(this::convertToDto)
                    .toList();
            dto.setChildren(categoryResponseDtos);
        }
        return dto;
    }
}
