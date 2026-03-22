package com.nishchay.identity_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String SECRET;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken=extractTokenFromCookies(request);
        if(jwtToken!=null){
            Claims claims= Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            String userName=claims.getSubject();

            UserDetails userDetails=userDetailsService.loadUserByUsername(userName);
            if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        filterChain.doFilter(request,response);
    }
    private String extractTokenFromCookies(HttpServletRequest request){
        if(request.getCookies()!=null){
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
