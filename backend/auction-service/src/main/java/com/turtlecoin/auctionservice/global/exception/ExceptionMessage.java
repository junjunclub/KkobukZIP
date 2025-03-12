package com.turtlecoin.auctionservice.global.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionMessage {
    String getMessage();
    HttpStatus getStatus();
}
