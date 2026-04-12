package com.nishchay.productservice.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.category.CreateCategoryRequestDto;
import com.nishchay.productservice.dto.product.CreateProductRequestDto;
import com.nishchay.productservice.dto.product.ProductResponseDto;
import com.nishchay.productservice.exception.ProductException;
import com.nishchay.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponce<?>> saveProduct(@ModelAttribute @Valid CreateProductRequestDto requestDto){
        try{
            ProductResponseDto responseDto=productService.saveProduct(requestDto);
            ApiResponce<ProductResponseDto> apiResponce=new ApiResponce<>(responseDto, HttpStatus.CREATED.value());
            return new ResponseEntity<>(apiResponce, HttpStatus.CREATED);
        }catch (Exception e){
            ApiResponce<String> responce=new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(responce, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponce<?>> getProductList(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size)
    {
        try{
            Page<ProductResponseDto> productList=productService.getProductList(page, size);
            ApiResponce<Page<ProductResponseDto>> apiResponce
        }catch (Exception e){
            ApiResponce<String> responce=new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(responce, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponce<?>> getProductById(@PathVariable("id") String id){
        try{
            ProductResponseDto responseDto=productService.getProductById(id);
            ApiResponce<ProductResponseDto> apiResponce=new ApiResponce<>(responseDto, HttpStatus.OK.value());
            return ResponseEntity.ok(apiResponce);
        }catch (ProductException pe){
            ApiResponce<String> responce=new ApiResponce<>(pe.getMessage(), pe.getStatus().value());
            return new ResponseEntity<>(responce, pe.getStatus());
        }catch (Exception e){
            ApiResponce<String> responce=new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(responce, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
