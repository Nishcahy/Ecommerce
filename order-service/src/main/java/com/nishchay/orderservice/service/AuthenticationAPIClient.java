package com.nishchay.orderservice.service;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.orderservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "IDENTITY-SERVICE")
public interface AuthenticationAPIClient {

    @GetMapping("/api/v1/auth/me")
    ResponseEntity<ApiResponce<UserDto>> getCurrentUser(@RequestHeader(HttpHeaders.COOKIE) String coockie);
}
