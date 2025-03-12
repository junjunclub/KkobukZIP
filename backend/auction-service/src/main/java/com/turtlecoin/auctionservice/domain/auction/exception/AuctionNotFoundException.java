package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class AuctionNotFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return AuctionExceptionMessage.AUCTION_NOT_FOUND.getMessage();
    }
    public HttpStatus getStatus() {
        return AuctionExceptionMessage.AUCTION_NOT_FOUND.getStatus();
    }
}
