package com.nishchay.identity_service.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.identity_service.dto.SignUpRequest;
import com.nishchay.identity_service.service.AuthService;
import com.nishchay.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.results.spi.ListResultsConsumer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public ResponseEntity<ApiResponce<String>>  addNewUser(@RequestBody SignUpRequest signUpRequest){

    }

}
