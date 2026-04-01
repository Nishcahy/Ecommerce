package com.nishchay.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;


@Component
public class RouterValidator {
    public static final List<String> openApiEndPoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/token",
            "/eureka"
    );
    public Predicate<ServerHttpRequest> isSecured =
            request -> {
                String path = request.getURI().getPath();

                System.out.println("Incoming path: " + path);

                return openApiEndPoints
                        .stream()
                        .noneMatch(path::startsWith);
            };
}
