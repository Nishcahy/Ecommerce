package com.nishchay.productservice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ProductException extends RuntimeException{
    private HttpStatus status;
    public ProductException(String msg,HttpStatus status){
        super(msg);
        this.status=status;
    }
}
