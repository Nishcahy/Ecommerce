package com.nishchay.productservice.handler;

import com.nishchay.commonlib.dto.ApiResponce;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponce<?>> handleValidateException(MethodArgumentNotValidException ex){
        Map<String,String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->
                errors.put(error.getField(),error.getDefaultMessage()));
        ApiResponce<Map<String,String>> apiResponce=new ApiResponce<>(errors, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiResponce,HttpStatus.BAD_REQUEST);
    }
}
