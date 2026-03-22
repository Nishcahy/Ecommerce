package com.nishchay.identity_service.service.impl;

import com.nishchay.identity_service.dto.AuthRequest;
import com.nishchay.identity_service.dto.SignUpRequest;
import com.nishchay.identity_service.entity.Role;
import com.nishchay.identity_service.entity.UserCredentials;
import com.nishchay.identity_service.enums.ERole;
import com.nishchay.identity_service.exception.AuthException;
import com.nishchay.identity_service.repository.RoleRepo;
import com.nishchay.identity_service.repository.UserCredentialRepo;
import com.nishchay.identity_service.service.AuthService;
import com.nishchay.identity_service.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepo userCredentialRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManger;
    @Override
    public String saveUser(SignUpRequest signUpRequest) {
        try{
            boolean existingUsername=checkExistingUsername(signUpRequest.getName());
            if(existingUsername){
                throw new AuthException("Username already exist", HttpStatus.BAD_REQUEST);
            }
            UserCredentials userCredentials=new UserCredentials();
            userCredentials.setName(signUpRequest.getName());
            userCredentials.setEmail(signUpRequest.getEmail());
            userCredentials.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            Set<Role> roles=new HashSet<>();
            if(signUpRequest.getRoles()==null){
                Role role=roleRepo.findByName(ERole.CUSTOMER).orElseThrow(()-> new RuntimeException("User Role not Found"));
                roles.add(role);
            }else{
                for(String roleName:signUpRequest.getRoles()){
                    ERole eRole;
                    try{
                        eRole=ERole.valueOf(roleName.toUpperCase());
                    }catch(IllegalArgumentException e){
                        throw  new RuntimeException("Role cannot be found");
                    }
                    Role role=roleRepo.findByName(eRole).orElseThrow(()->new RuntimeException("Role cannot be found"));
                    roles.add(role);
                }
            }
            userCredentials.setRoles(roles);
            userCredentialRepo.save(userCredentials);
            return "User registered successfully";


        } catch (Exception e) {
            throw new RuntimeException("User Registration failed "+e.getMessage());
        }

    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    @Override
    public String generateToken(AuthRequest authRequest, HttpServletResponse httpServletResponse) {
        Authentication authentication=authenticationManger.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),authRequest.getPassword()));
        Optional<UserCredentials> user=userCredentialRepo.findByName(authentication.getName());
        if(!user.isPresent()){
            throw new AuthException("Invalid Credentials..Please try agian!!!",HttpStatus.UNAUTHORIZED);
        }
        UserCredentials userCredentials=user.get();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken=jwtService.generateToken(authentication);

        Cookie cookie=new Cookie("token",jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30*60);
        httpServletResponse.addCookie(cookie);

        return jwtToken;

    }

    private boolean checkExistingUsername(String username){
       return userCredentialRepo.findByName(username).isPresent();
    }
}
