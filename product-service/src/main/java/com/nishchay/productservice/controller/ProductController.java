package com.nishchay.productservice.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.productservice.dto.category.CategoryResponseDto;
import com.nishchay.productservice.dto.category.CreateCategoryRequestDto;
import com.nishchay.productservice.dto.product.CreateProductRequestDto;
import com.nishchay.productservice.dto.product.ProductResponseDto;
import com.nishchay.productservice.dto.product.UpdateProductRequestDto;
import com.nishchay.productservice.exception.ProductException;
import com.nishchay.productservice.service.ProductService;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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

    @GetMapping
    public ResponseEntity<ApiResponce<?>> getProductList(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size)
    {
        try{
            Page<ProductResponseDto> productList=productService.getProductList(page, size);
            ApiResponce<Page<ProductResponseDto>> apiResponce=new ApiResponce<>(productList, HttpStatus.OK.value());
            return ResponseEntity.ok(apiResponce);
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


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponce<?>> updateProduct(@PathVariable("id") String id, @RequestBody UpdateProductRequestDto requestDto,@RequestHeader(HttpHeaders.IF_MATCH) int version){
        try{
            ProductResponseDto responseDto=productService.updateProduct(id, requestDto, version);
            ApiResponce<ProductResponseDto> apiResponce=new ApiResponce<>(responseDto, HttpStatus.OK.value());
            return ResponseEntity.ok(apiResponce);
        }catch(OptimisticLockException op){
            ApiResponce<String> responce=new ApiResponce<>(op.getMessage(), HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(responce, HttpStatus.CONFLICT);
        }catch (Exception e){
            ApiResponce<String> apiResponce=new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponce, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponce<?>> deleteProduct(@PathVariable("id") String id){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }catch (ProductException pe){
            ApiResponce<String> responce=new ApiResponce<>(pe.getMessage(), pe.getStatus().value());
            return new ResponseEntity<>(responce, pe.getStatus());
        }catch (Exception e){
            ApiResponce<String> responce=new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(responce, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponce<Page<ProductResponseDto>>> searchProducts(
            @RequestParam(required = false) String  name,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable
            ){
            Page<ProductResponseDto> products=productService.searchProducts(name, categoryId, minPrice, maxPrice, pageable);
            ApiResponce<Page<ProductResponseDto>> apiResponce=new ApiResponce<>(products, HttpStatus.OK.value());
            return ResponseEntity.ok(apiResponce);
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponce<?>> getProductsByIds(@RequestParam("ids") Set<String> productIds) {
        try {
            List<ProductResponseDto> productDTOs = productService.getProductsByIds(productIds);
            ApiResponce<List<ProductResponseDto>> apiResponse = new ApiResponce<>(productDTOs, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponce<String> response = new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
