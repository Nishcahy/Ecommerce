package com.nishchay.identity_service.config;

import com.nishchay.identity_service.entity.UserCredentials;
import com.nishchay.identity_service.repository.UserCredentialRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredentialRepo repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredentials credentials=repo.findByName(username).orElseThrow(()->new UsernameNotFoundException("User not found with name "+username));
        return CustomUserDetails.build(credentials);
    }
}
