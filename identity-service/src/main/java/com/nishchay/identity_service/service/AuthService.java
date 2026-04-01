package com.nishchay.identity_service.service;

import com.nishchay.identity_service.dto.AuthRequest;
import com.nishchay.identity_service.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthService {
    String saveUser(SignUpRequest userCredential);
    String generateToken(AuthRequest authRequest, HttpServletResponse response);
    void validateToken(String token);
}
