package com.nishchay.identity_service.service;


import com.nishchay.identity_service.dto.UserDto;

public interface UserService {
    UserDto getUserByUsername(String username);
}
