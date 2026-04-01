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
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponce<String>> addNewUser(@RequestBody SignUpRequest signUpRequest) {
        try {
            String message = authService.saveUser(signUpRequest);
            ApiResponce<String> apiResponse = new ApiResponce<>(message, HttpStatus.CREATED.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        }
        catch(AuthException e){
            ApiResponce<String> apiResponse = new ApiResponce<>(e.getMessage(), e.getStatus().value());
            return new ResponseEntity<>(apiResponse, e.getStatus());
        }
        catch(Exception e){
            ApiResponce<String> apiResponse = new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponce<String>> getToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authenticate.isAuthenticated()) {
                String generateToken = authService.generateToken(authRequest, response);

                ApiResponce<String> apiResponse = new ApiResponce<>(generateToken, HttpStatus.OK.value());
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                ApiResponce<String> apiResponse = new ApiResponce<>("Invalid access!", HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);            }
        }catch(Exception e){
            ApiResponce<String> apiResponse = new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponce<String>> validateToken(@RequestParam("token") String token) {
        try {
            authService.validateToken(token);
            ApiResponce<String> apiResponse = new ApiResponce<>("Token is valid", HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch(Exception e){
            ApiResponce<String> apiResponse = new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponce<?>> getCurrentUser(@AuthenticationPrincipal UserDetails currentUser) {
        try {
            UserDto userDto = userService.getUserByUsername(currentUser.getUsername());
            ApiResponce<UserDto> apiResponse = new ApiResponce<>(userDto, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponce<String> apiResponse = new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
