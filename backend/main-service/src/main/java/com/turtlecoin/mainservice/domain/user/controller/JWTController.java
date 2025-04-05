package com.turtlecoin.mainservice.domain.user.controller;


import com.turtlecoin.mainservice.domain.user.service.JWTService;
import com.turtlecoin.jwt.JWTUtil;
import com.turtlecoin.mainservice.domain.user.util.JWTValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/main/jwt")
@RequiredArgsConstructor
public class JWTController {

    private final JWTService jwtService;

    // Refresh Token 재발급만 처리
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return jwtService.refreshTokenRotate(refreshToken);
    }
}
