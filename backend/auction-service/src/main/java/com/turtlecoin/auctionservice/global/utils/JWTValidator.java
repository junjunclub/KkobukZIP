package com.turtlecoin.auctionservice.global.utils;

import com.turtlecoin.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

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

    public boolean validateAccessToken(String token) {
        try {
            return !jwtUtil.isTokenExpired(token) &&
                    "access".equals(jwtUtil.getClaim(token, "category", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            String[] tokens = token.split(" ");
            if (tokens.length != 2 || !tokens[0].equalsIgnoreCase("Bearer")) {
                return false;
            }
            token = tokens[1];
            String username = jwtUtil.getClaim(token, "username", String.class);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String stored = ops.get(username);
            return token.equals(stored);
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromBearer(String token) {
        return jwtUtil.getClaim(token.substring(7), "id", Long.class);
    }

    public String extractToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
