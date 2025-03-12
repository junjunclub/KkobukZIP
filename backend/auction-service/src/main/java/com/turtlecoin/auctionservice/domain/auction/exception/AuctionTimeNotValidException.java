package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class AuctionTimeNotValidException extends RuntimeException {
    @Override
    public String getMessage() {
        return AuctionExceptionMessage.AUCTION_TIME_NOT_VALID.getMessage();
    }
    public HttpStatus getStatus() {
        return AuctionExceptionMessage.AUCTION_TIME_NOT_VALID.getStatus();
    }
}
