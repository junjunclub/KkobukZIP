package com.turtlecoin.apigatewayservice.config;

import com.turtlecoin.apigatewayservice.filter.JwtAuthenticationFilter;
import com.turtlecoin.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil(secret);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JWTUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }
}