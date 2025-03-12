package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class TurtleNotOwnedException extends RuntimeException {
    @Override
    public String getMessage() {
        return TurtleExceptionMessage.TURTLE_NOT_OWNED.getMessage();
    }
    public HttpStatus getStatus() {
        return TurtleExceptionMessage.TURTLE_NOT_OWNED.getStatus();
    }
}
