package com.nishchay.orderservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderException extends RuntimeException {
    private HttpStatus status;

    public OrderException(String msg,HttpStatus status){
        super(msg);
        this.status=status;
    }

}
