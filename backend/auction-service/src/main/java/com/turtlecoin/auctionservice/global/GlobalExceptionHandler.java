package com.turtlecoin.auctionservice.global;

import com.turtlecoin.auctionservice.global.exception.BusinessException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 1. 비즈니스 예외 처리 (AuctionNotFoundException, PhotoNotUploadedException 등 포함)
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("[비즈니스 예외] {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }

    // ✅ 2. Feign 클라이언트 오류 처리 (503 Service Unavailable)
    @ExceptionHandler(FeignException.class)
    public ApiResponse<Void> handleFeignException(FeignException e) {
        log.error("[FeignException] Main-Service가 응답하지 않음: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE, "Main-Service가 응답하지 않습니다.");
    }

    // ✅ 3. 파일 처리 예외 (IOException)
    @ExceptionHandler(IOException.class)
    public ApiResponse<Void> handleIOException(IOException e) {
        log.error("[IOException] 파일 처리 중 오류 발생: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다.");
    }

    // ✅ 4. 서버 내부 오류 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGeneralException(Exception e) {
        log.error("[서버 내부 오류] {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }
}
