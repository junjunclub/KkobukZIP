package com.turtlecoin.auctionservice.domain.auction.exception;

import org.springframework.http.HttpStatus;

public class PhotoNotUploadedException extends RuntimeException {
    @Override
    public String getMessage() {
        return AuctionExceptionMessage.PHOTO_NOT_UPLOADED.getMessage();
    }
    public HttpStatus getStatus() {
        return AuctionExceptionMessage.PHOTO_NOT_UPLOADED.getStatus();
    }
}
