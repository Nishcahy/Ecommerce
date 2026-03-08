package com.nishchay.identity_service.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException{

    private HttpStatus httpStatus;
    public AuthException(String message,HttpStatus httpStatus){
        super(message);
        this.httpStatus=httpStatus;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }

}
