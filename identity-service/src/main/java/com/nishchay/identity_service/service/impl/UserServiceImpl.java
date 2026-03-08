package com.nishchay.identity_service.service.impl;

import com.nishchay.identity_service.dto.UserDto;
import com.nishchay.identity_service.entity.UserCredentials;
import com.nishchay.identity_service.repository.UserCredentialRepo;
import com.nishchay.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserCredentialRepo userCredentialRepo;
    private final ModelMapper modelMapper;

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public UserDto getUserByUsername(String userName) {
        UserCredentials userCredential=userCredentialRepo.findByName(userName).orElse(null);
        if(userCredential != null){
            logger.info("User cRedentials are {}",userCredential);
            return modelMapper.map(userCredential,UserDto.class);
        }
        return null;
    }
}
