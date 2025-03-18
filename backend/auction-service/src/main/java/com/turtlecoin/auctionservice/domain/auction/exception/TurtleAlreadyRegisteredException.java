package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.turtlecoin.auctionservice.domain.auction.exception.TurtleExceptionMessage.*;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TurtleAlreadyRegisteredException extends RuntimeException {
    @Override
    public String getMessage() {
        return TURTLE_ALREADY_REGISTERED.getMessage();
    }
    public HttpStatus getStatus() {
        return TURTLE_ALREADY_REGISTERED.getStatus();
    }
}
