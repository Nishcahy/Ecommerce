package com.nishchay.identity_service.controller;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.identity_service.dto.AuthRequest;
import com.nishchay.identity_service.dto.SignUpRequest;
import com.nishchay.identity_service.dto.UserDto;
import com.nishchay.identity_service.exception.AuthException;
import com.nishchay.identity_service.service.AuthService;
import com.nishchay.identity_service.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponce<String>>  addNewUser(@RequestBody SignUpRequest signUpRequest){
        try{
            String message=authService.saveUser(signUpRequest);
            ApiResponce<String> apiResponce=new ApiResponce<>(message, HttpStatus.CREATED.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.CREATED);
        }
        catch (AuthException e){
            ApiResponce<String> apiResponce=new ApiResponce<>(e.getMessage(),e.getHttpStatus().value());
            return new ResponseEntity<>(apiResponce,e.getHttpStatus());
        }catch (Exception e){
            ApiResponce<String> apiResponce=new ApiResponce<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/token")
    public ResponseEntity<ApiResponce<String>> getToken(@RequestBody AuthRequest authRequest, HttpServletResponse response){
        try{
            Authentication authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),authRequest.getPassword()));
            if(authentication.isAuthenticated()){
                String generatToken=authService.generateToken(authRequest,response);

                ApiResponce<String> apiResponce=new ApiResponce<>(generatToken,HttpStatus.OK.value());
                return new ResponseEntity<>(apiResponce,HttpStatus.OK);
            }else{
                ApiResponce<String> apiResponce=new ApiResponce<>("Invalid Access!!",HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(apiResponce,HttpStatus.OK);
            }
        } catch (Exception e) {
            ApiResponce<String> apiResponce=new ApiResponce<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/token")
    public ResponseEntity<ApiResponce<String>> validateToken(@RequestParam("token") String token){
        try{
            authService.validateToken(token);
            ApiResponce<String> apiResponce=new ApiResponce<>(token,HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.OK);
        }catch (Exception e){
            ApiResponce<String> apiResponce=new ApiResponce<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails currentUser){
        try{
            UserDto userDto=userService.getUserByUsername(currentUser.getUsername());
            ApiResponce<UserDto> apiResponce=new ApiResponce(userDto,HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.OK);
        }catch (Exception e){
            ApiResponce<String> apiResponce=new ApiResponce(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponce,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
