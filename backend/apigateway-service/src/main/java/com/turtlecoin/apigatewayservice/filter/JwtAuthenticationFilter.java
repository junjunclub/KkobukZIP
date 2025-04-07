package com.turtlecoin.apigatewayservice.filter;

import com.turtlecoin.apigatewayservice.util.JwtValidator;
import com.turtlecoin.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements GatewayFilter {

    private final JWTUtil jwtUtil;

    public JwtAuthenticationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        if (path.startsWith("/main/user/login") ||
                path.startsWith("/main/user/join") ||
                path.startsWith("/main/user/email")) {
            return chain.filter(exchange); // 필터 건너뜀
        }

        // Authorization 헤더 확인
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
        }

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        token = token.substring(7); // Bearer 제거

        try {
            if (jwtUtil.isTokenExpired(token)) {
                return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", jwtUtil.getClaim(token, "id", Long.class).toString())
                .header("X-User-Email", jwtUtil.getClaim(token, "username", String.class))
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        // 유효한 경우 다음 필터로 넘어감
        return chain.filter(modifiedExchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}