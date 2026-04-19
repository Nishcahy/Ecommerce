package com.nishchay.productservice.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.productservice.dto.product_variant.CreateProductVariantRequestDto;
import com.nishchay.productservice.dto.product_variant.ProductVariantResponseDto;
import com.nishchay.productservice.dto.product_variant.UpdateProductVariantRequestDto;
import com.nishchay.productservice.entity.ProductVariant;
import com.nishchay.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/variant")
public class ProductVariantController {

    private final ProductVariantService productVariantService;
    private final ModelMapper modelMapper;

    @PostMapping("/{productId}")
    public ResponseEntity<ProductVariantResponseDto> createVariant(@PathVariable("productId") String productId, @RequestBody CreateProductVariantRequestDto requestDto){
        ProductVariantResponseDto responseDto=productVariantService.createProductVariant(productId, requestDto.getAttributes(), requestDto.getPrice(), requestDto.getSku(), requestDto.getInitialStock(), requestDto.getReorderLevel());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("{productId}")
    public ResponseEntity<List<ProductVariantResponseDto>> getVariants(@PathVariable String productId) {
        List<ProductVariantResponseDto> variants = productVariantService.getVariantsByProductId(productId);
        return ResponseEntity.ok(variants);
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponseDto>> getVariantsByIds(@RequestParam("variantIds") Set<Long> variantIds) {
        List<ProductVariant> variants = productVariantService.getProductVariantByIds(variantIds);
        return ResponseEntity.ok(variants.stream().map(productVariant -> modelMapper.map(productVariant,ProductVariantResponseDto.class)).collect(Collectors.toList()));
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<ApiResponce<?>> updateVariant(@PathVariable Long variantId, @RequestBody UpdateProductVariantRequestDto requestDto){
        try {
            ProductVariantResponseDto responseDto = productVariantService.updateProductVariant(variantId, requestDto);
            return new ResponseEntity<>(new ApiResponce<>(responseDto, HttpStatus.OK.value()), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{variantId}")
    public ResponseEntity<ApiResponce<?>> deleteVariant(@PathVariable Long variantId){
        try {
            productVariantService.deleteProductVariant(variantId);
            return new ResponseEntity<>(new ApiResponce<>(null, HttpStatus.NO_CONTENT.value()), HttpStatus.NO_CONTENT);
        }catch(Exception e){
            return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
