package com.turtlecoin.auctionservice.global;

import com.turtlecoin.auctionservice.domain.auction.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<Void> UserNotFoundException(UserNotFoundException e) {
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }
}
