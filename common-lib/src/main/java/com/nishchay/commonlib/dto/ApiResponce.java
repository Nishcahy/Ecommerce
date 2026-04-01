package com.nishchay.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponce<T> {
    private LocalDateTime timeStamp;
    private T data;
    private int statusCode;

    public ApiResponce(T data,int statusCode){
        this.timeStamp=LocalDateTime.now();
        this.data=data;
        this.statusCode=statusCode;

    }

    public void setTimeStamp(LocalDateTime timeStamp){
        this.timeStamp=timeStamp;
    }
}
