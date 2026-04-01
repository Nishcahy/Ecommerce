package com.nishchay.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nishchay.apigateway.util.JwtUtil;
import com.nishchay.commonlib.dto.ApiResponce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class RoleBasedAccessFilter extends AbstractGatewayFilterFactory<RoleBasedAccessFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedAccessFilter.class);

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;  // Inject ObjectMapper here

    public RoleBasedAccessFilter(JwtUtil jwtUtil,ObjectMapper objectMapper){
        super(Config.class);
        this.jwtUtil=jwtUtil;
        this.objectMapper=objectMapper;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();


            String token = extractTokenFromCookies(request);

            if (token == null) {
                return onError(exchange, "Missing or invalid token", HttpStatus.UNAUTHORIZED);
            }

            try {

                jwtUtil.validateToken(token);

                List<String> roles = jwtUtil.extractRoles(token);
                List<String> permissions = jwtUtil.extractPermissions(token);

                boolean hasRole = roles.stream().anyMatch(config.getRequiredRoles()::contains);

                boolean hasPermission = permissions.stream().anyMatch(config.getRequiredPermissions()::contains);
                if (!hasRole) {
                    return onError(exchange, "Forbidden access", HttpStatus.FORBIDDEN);
                }

                if(config.getRequiredPermissions().size() == 0){
                    return chain.filter(exchange);
                }

                if(!hasPermission){
                    return onError(exchange, "You don't have permission to do this.", HttpStatus.UNAUTHORIZED);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                return onError(exchange, "Unauthorized access", HttpStatus.UNAUTHORIZED);
            }


            return chain.filter(exchange);
        };
    }

    private String extractTokenFromCookies(ServerHttpRequest request) {
        return request.getCookies().getFirst("token") != null ?
                request.getCookies().getFirst("token").getValue() : null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponce<String> apiResponse = new ApiResponce<>(errorMessage, httpStatus.value());
        try {
            byte[] bytes = objectMapper.writeValueAsString(apiResponse).getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public static class Config {
        private List<String> requiredPermissions;
        private List<String> requiredRoles;
        private List<String> methods;

        public List<String> getMethods() {
            return methods;
        }

        public void setMethods(List<String> methods) {
            this.methods = methods;
        }

        public List<String> getRequiredPermissions() {
            return requiredPermissions != null ? requiredPermissions : Collections.emptyList();
        }

        public void setRequiredPermissions(List<String> requiredPermissions) {
            this.requiredPermissions = requiredPermissions;
        }

        public List<String> getRequiredRoles() {
            return requiredRoles;
        }

        public void setRequiredRoles(List<String> requiredRoles) {
            this.requiredRoles = requiredRoles;
        }
    }
}