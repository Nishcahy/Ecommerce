package com.nishchay.productservice.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.category.CreateCategoryRequestDto;
import com.nishchay.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponce<CategoryResponseDto>> createCategory(@RequestBody CreateCategoryRequestDto requestDto){
        CategoryResponseDto categoryResponseDto=categoryService.createCategory(requestDto);
        return ResponseEntity.ok(new ApiResponce<>(categoryResponseDto, HttpStatus.CREATED.value()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponce<CategoryResponseDto>> updateCategory( @PathVariable String id,@RequestBody CreateCategoryRequestDto requestDto){
        CategoryResponseDto categoryResponseDto=categoryService.updateCategory(id, requestDto);
        return ResponseEntity.ok(new ApiResponce<>(categoryResponseDto, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteCategory(@PathVariable String id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponce<CategoryResponseDto>> getCategoryById(@PathVariable String id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        ApiResponce<CategoryResponseDto> response = new ApiResponce<>(category, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponce<List<CategoryResponseDto>>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        ApiResponce<List<CategoryResponseDto>> response = new ApiResponce<>(categories, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponce<List<CategoryResponseDto>>> getRootCategories() {
        List<CategoryResponseDto> rootCategories = categoryService.getRootCategories();
        ApiResponce<List<CategoryResponseDto>> response = new ApiResponce<>(rootCategories, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
