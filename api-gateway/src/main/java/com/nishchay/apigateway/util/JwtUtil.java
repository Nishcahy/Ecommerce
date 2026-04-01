package com.nishchay.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;


@Component
public class JwtUtil {
    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public void validateToken(String token) {
        Jwts.parser().setSigningKey(getSigninKey()).build().parseClaimsJws(token);
    }

    public List<String> extractRoles(String token){
        Claims claims=getClaims(token);
        List<String> rolesClaim=claims.get("roles",List.class);

        List<String> roles=new ArrayList<>();
        if(rolesClaim!=null){
            for(String role:rolesClaim){
                if(role!=null){
                    roles.add(role);
                }
            }
        }
        return roles;

    }
    public List<String> extractPermissions(String token){
        Claims claims=getClaims(token);
        List<String> permissionClaim=claims.get("permissions",List.class);

        List<String> permissions=new ArrayList<>();
        if(permissionClaim !=null){
            for (String permission:permissionClaim){
                if(permission != null){
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }
    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSigninKey(){
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
