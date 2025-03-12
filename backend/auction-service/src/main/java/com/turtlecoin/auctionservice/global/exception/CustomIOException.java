package com.turtlecoin.auctionservice.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomIOException extends RuntimeException {
    private final HttpStatus status;

    public CustomIOException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomIOException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.getMessage());
        this.status = exceptionMessage.getStatus();
    }
}
