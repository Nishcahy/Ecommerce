package com.nishchay.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponce<T> {
    private LocalDateTime localDateTime;
    private T data;
    private int statusCode;

    public ApiResponce(T data,int statusCode){
        this.localDateTime=LocalDateTime.now();
        this.data=data;
        this.statusCode=statusCode;

    }

}
