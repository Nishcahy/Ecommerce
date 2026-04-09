package com.nishchay.productservice.exception;

import org.springframework.http.HttpStatus;

public class ProductException extends RuntimeException{
    private HttpStatus status;
    public ProductException(String msg,HttpStatus status){
        super(msg);
        this.status=status;
    }
}
