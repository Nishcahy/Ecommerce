package com.nishchay.identity_service.service.impl;

import com.nishchay.identity_service.config.CustomUserDetails;
import com.nishchay.identity_service.entity.UserCredentials;
import com.nishchay.identity_service.repository.UserCredentialRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialRepo userCredentialRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredentials userCredentials=userCredentialRepo.findByName(username)
                .orElseThrow(()->new UsernameNotFoundException("User Cannot found with name "+username));
        return CustomUserDetails.build(userCredentials);
    }
}
