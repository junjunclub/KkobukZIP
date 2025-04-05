package com.turtlecoin.auctionservice.global.config;

import com.turtlecoin.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTConfig {

    @Value("${spring.jwt.secret}")
    String secret;

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil(secret);
    }
}

