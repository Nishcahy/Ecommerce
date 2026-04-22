package com.nishchay.productservice.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.productservice.dto.attribute.UpdateAttributeRequest;
import com.nishchay.productservice.entity.Attribute;
import com.nishchay.productservice.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attribute")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @PostMapping
    public ResponseEntity<ApiResponce<?>> createAttribute(@RequestBody UpdateAttributeRequest updateAttributeRequest){
        Attribute createdAttribute=attributeService.createAttribute(updateAttributeRequest.getName(), updateAttributeRequest.getDataType());
        return new ResponseEntity<>(new ApiResponce<>(createdAttribute, HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponce<?>> updateAttribute(@PathVariable Long id,@RequestBody UpdateAttributeRequest updateAttributeRequest){
        Attribute updatedAttribute=attributeService.updateAttribute(id, updateAttributeRequest.getName(), updateAttributeRequest.getDataType());
        return new ResponseEntity<>(new ApiResponce<>(updatedAttribute, HttpStatus.OK.value()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id){
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponce<?>> getAllAttributes(){
        return new ResponseEntity<>(new ApiResponce<>(attributeService.getAllAttributes(), HttpStatus.OK.value()), HttpStatus.OK);
    }

}
