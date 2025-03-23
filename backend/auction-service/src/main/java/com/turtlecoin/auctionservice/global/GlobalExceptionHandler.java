package com.turtlecoin.auctionservice.global;

import com.turtlecoin.auctionservice.domain.auction.exception.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request
    @ExceptionHandler(AuctionAlreadyFinishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleAuctionAlreadyFinishedException(AuctionAlreadyFinishedException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(AuctionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleAuctionNotFoundException(AuctionNotFoundException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(AuctionTimeNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleAuctionTimeNotValidException(AuctionTimeNotValidException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(PhotoNotUploadedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handlePhotoNotUploadedException(PhotoNotUploadedException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error(e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    // 409 - Conflict
    @ExceptionHandler(TurtleAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleTurtleAlreadyRegisteredException(TurtleAlreadyRegisteredException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }
}
