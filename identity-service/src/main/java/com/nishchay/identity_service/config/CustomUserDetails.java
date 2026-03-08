package com.nishchay.identity_service.config;

import com.nishchay.identity_service.entity.UserCredentials;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Set<String> permissions;

    public CustomUserDetails(Long id,String username,String password,String email,Collection<? extends GrantedAuthority> authorities,Set<String> permissions){
        this.id=id;
        this.username=username;
        this.permissions=permissions;
        this.password=password;
        this.email=email;
        this.authorities=authorities;
    }

    public CustomUserDetails(UserCredentials userCredentials){
        this.username=userCredentials.getName();
        this.password=userCredentials.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public  List<String> getRoleNames(){
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails build(UserCredentials user){
        List<GrantedAuthority> authorities=user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());

        Set<String> permissions=user.getPermissions().stream()
                .map(permission -> permission.getName().name())
                .collect(Collectors.toSet());

        return new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getEmail(),
                authorities,
                permissions
        );


    }

    @Override
    public  boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o==null || getClass()!=o.getClass()){
            return false;
        }
        CustomUserDetails user=(CustomUserDetails) o;
        return Objects.equals(id,user.id);
    }

    public int hashCode(){
        return Objects.hash(id);
    }
}
