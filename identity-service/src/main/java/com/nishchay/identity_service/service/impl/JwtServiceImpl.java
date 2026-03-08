package com.nishchay.identity_service.service.impl;

import com.nishchay.identity_service.config.CustomUserDetails;
import io.jsonwebtoken.*;
import com.nishchay.identity_service.service.JwtService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    public static final  String  SECRET="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    @Override
    public void validateToken(String token) {

        try{
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
        }catch (SecurityException | MalformedJwtException e){
            throw new AuthenticationCredentialsNotFoundException("JWt Expored or incorrect");
        }catch (ExpiredJwtException e){
            throw new AuthenticationCredentialsNotFoundException("Jwt Token Expired");
        }catch (UnsupportedJwtException e){
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token");
        }catch (Exception e){
            throw new RuntimeException("Exception occured for JWT token "+e.getMessage());
        }

    }

    @Override
    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal= (CustomUserDetails) authentication.getPrincipal();
        List<String> roles=userPrincipal.getRoleNames();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles",roles)
                .claim("permissions",userPrincipal.getPermissions())
                .claim("email",userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(getSignKey(),SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey(){
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private SecretKey getSecretKey(){
        byte[] keyBytes= Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
