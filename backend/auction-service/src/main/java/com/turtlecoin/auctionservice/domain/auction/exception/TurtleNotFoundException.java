package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class TurtleNotFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return TurtleExceptionMessage.TURTLE_NOT_FOUND.getMessage();
    }
    public HttpStatus getStatus() {
        return TurtleExceptionMessage.TURTLE_NOT_FOUND.getStatus();
    }
}