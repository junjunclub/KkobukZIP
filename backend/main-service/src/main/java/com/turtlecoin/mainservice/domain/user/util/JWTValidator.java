package com.turtlecoin.mainservice.domain.user.util;

import com.turtlecoin.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class JWTValidator {

    private final RedisTemplate<String, String> redisTemplate;
    private final JWTUtil jwtUtil;

    public JWTValidator(
            @Value("${spring.jwt.secret}") String secret,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = new JWTUtil(secret);
    }

    public boolean validateAccessToken(String rawToken) {
        String token = extractToken(rawToken);
        try {
            return !jwtUtil.isTokenExpired(token) &&
                    "access".equals(jwtUtil.getClaim(token, "category", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String rawToken) {
        try {
            String token = extractToken(rawToken);
            String username = jwtUtil.getClaim(token, "username", String.class);
            String storedToken = redisTemplate.opsForValue().get(username);
            return token.equals(storedToken);
        } catch (Exception e) {
            return false;
        }
    }

    private String extractToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        }
        throw new IllegalArgumentException("Invalid token format");
    }
}
