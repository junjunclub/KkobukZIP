package com.turtlecoin.mainservice.domain.user.service;

import com.turtlecoin.jwt.JWTUtil;
import com.turtlecoin.mainservice.domain.user.entity.User;
import com.turtlecoin.mainservice.domain.user.exception.IssueTokenException;
import com.turtlecoin.mainservice.domain.user.exception.UserNotFoundException;
import com.turtlecoin.mainservice.domain.user.repository.UserRepository;
import com.turtlecoin.mainservice.domain.user.util.JWTValidator;
import com.turtlecoin.mainservice.global.exception.RedisSaveException;
import com.turtlecoin.mainservice.global.response.ResponseVO;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JWTService {
    private final JWTUtil jwtUtil;
    private final JWTValidator jwtValidator;
    private final RedisTemplate<String,String> redisTemplate;
    private final UserRepository userRepository;

    public Map<String,Object> issueToken(Optional<User> user) throws RedisSaveException, IssueTokenException {
        try{
            // 인증 성공 시 JWT 토큰 생성 및 Redis 저장
            String email = user.get().getEmail(); // "username"을 "email"로 변경;
            String role = user.get().getRole().toString();
            String uuid = user.get().getUuid();
            Long id = user.get().getId();

            // Access token과 Refresh token 생성
            String access = jwtUtil.createToken("access", email, role,id, uuid,6000000L);
            String refresh = jwtUtil.createToken("refresh", email, role, id, uuid,86400000L);

            if(access==""||refresh==""){
                throw new IssueTokenException("토큰 생성 과정에서 에러가 발생하였습니다.");
            }
            // Redis에 refresh token 저장 (email, 토큰, 만료시간을 함께 저장)
            redisTemplate.opsForValue().set(email, refresh, 86400000L, TimeUnit.SECONDS);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", access);
            data.put("refreshToken", refresh);
            data.put("role", role);
            data.put("uuid",user.get().getUuid());
            data.put("nickname", user.get().getNickname());
            data.put("email", user.get().getEmail());
            data.put("address",user.get().getAddress());
            data.put("phoneNumber",user.get().getPhonenumber());
            data.put("profileImage",user.get().getProfileImage());
            data.put("userId",user.get().getId());
            data.put("foreignFlag",user.get().getForeignFlag());
            data.put("name",user.get().getName());
            data.put("birth",user.get().getBirth());
            return data;

        }
        catch(IssueTokenException e){
            throw new IssueTokenException(e.getMessage());
        } catch (Exception e){
            throw new RedisSaveException("Redis에 토큰을 저장하는 과정에서 에러가 발생하였습니다.");
        }

    }

    // 로그아웃 요청시 redis에서 refreshToken 제거
    public ResponseEntity<?> logoutService(String RefreshToken) {
        // Refresh token 검증
        if (!jwtValidator.validateRefreshToken(RefreshToken)) {
            return new ResponseEntity<>(ResponseVO.failure("400", "로그아웃 처리 중 오류가 발생했습니다."), HttpStatus.BAD_REQUEST);
        }

        // SecurityContext 클리어 및 Redis에서 Refresh Token 삭제
        SecurityContextHolder.clearContext();
        redisTemplate.delete(RefreshToken);

        return new ResponseEntity<>(ResponseVO.success("정상적으로 로그아웃 처리가 완료되었습니다."), HttpStatus.OK);
    }
    // accessToken 재발급 요청
    public ResponseEntity<?> refreshTokenRotate(String refreshToken){
        //refreshToken 유효성 확인
        if(refreshToken == null) {
            return new ResponseEntity<>(ResponseVO.failure("400","refresh token null"), HttpStatus.BAD_REQUEST);
        }

        // Check token format
        String[] parts = refreshToken.split(" ");
        if (parts.length == 2 && "Bearer".equals(parts[0])) {
            refreshToken = parts[1];
        }else{
            return new ResponseEntity<>(ResponseVO.failure("400","Invalid token format"), HttpStatus.BAD_REQUEST);
        }

        // Check Token expired
        try {
            jwtUtil.isTokenExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(ResponseVO.failure("400","refresh token expired"), HttpStatus.UNAUTHORIZED);
        }

        // Redis에서 refresh token 조회
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String storedToken = valueOps.get(jwtUtil.getClaim(refreshToken, "username", String.class)); // Key 패턴에 맞게 수정

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            return new ResponseEntity<>(ResponseVO.failure("400","Invalid refresh token"), HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getClaim(refreshToken, "username", String.class);
        String role = jwtUtil.getClaim(refreshToken, "role", String.class);
        Long id = jwtUtil.getClaim(refreshToken, "id", Long.class);
        String uuid = jwtUtil.getClaim(refreshToken, "uuid", String.class);

        //make new JWT
        String newAccess = jwtUtil.createToken("access",username,role,id,uuid,600000L);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status",200);
        responseBody.put("message","요청이 정상적으로 처리되었습니다.");

        Map<String, String> data = new HashMap<>();
        data.put("accessToken", newAccess);
        responseBody.put("data", data);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    public User getUserFromToken(String token) {
        Long userId = jwtUtil.getClaim(token, "id", Long.class);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
    }


    public Long getIdFromToken(String token) {
        return jwtUtil.getIdFromToken(token);
    }
}
