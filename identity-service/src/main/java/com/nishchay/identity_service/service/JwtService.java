package com.nishchay.identity_service.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
    void validateToken(final String token);
    String generateToken(Authentication authentication);
}
