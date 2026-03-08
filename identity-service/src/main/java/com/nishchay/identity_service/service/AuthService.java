package com.nishchay.identity_service.service;

import com.nishchay.identity_service.dto.AuthRequest;
import com.nishchay.identity_service.dto.SignUpRequest;
import com.nishchay.identity_service.entity.UserCredentials;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    String saveUser(SignUpRequest signUpRequest);
    void validateToken(String token);
    String generateToken(AuthRequest authRequest, HttpServletResponse httpServletResponse);
}