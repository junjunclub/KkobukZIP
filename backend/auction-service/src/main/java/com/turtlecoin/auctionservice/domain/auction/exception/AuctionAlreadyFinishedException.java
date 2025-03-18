package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class AuctionAlreadyFinishedException extends RuntimeException {
    @Override
    public String getMessage() {
        return AuctionExceptionMessage.AUCTION_ALREADY_FINISHED.getMessage();
    }
    public HttpStatus getStatus() {
        return AuctionExceptionMessage.AUCTION_ALREADY_FINISHED.getStatus();
    }
}
