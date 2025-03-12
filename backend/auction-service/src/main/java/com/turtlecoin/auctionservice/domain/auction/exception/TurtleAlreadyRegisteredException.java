package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class TurtleAlreadyRegisteredException extends RuntimeException {
    @Override
    public String getMessage() {
        return TurtleExceptionMessage.TURTLE_ALREADY_REGISTERED.getMessage();
    }
    public HttpStatus getStatus() {
        return TurtleExceptionMessage.TURTLE_ALREADY_REGISTERED.getStatus();
    }
}
