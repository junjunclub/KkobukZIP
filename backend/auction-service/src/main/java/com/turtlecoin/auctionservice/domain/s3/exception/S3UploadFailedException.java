package com.turtlecoin.auctionservice.domain.s3.exception;

import org.springframework.http.HttpStatus;

import java.io.IOException;

public class S3UploadFailedException extends RuntimeException {
    @Override
    public String getMessage() {
        return S3ExceptionMessage.S3_UPLOAD_FAILED.getMessage();
    }
    public HttpStatus getStatus() {
        return S3ExceptionMessage.S3_UPLOAD_FAILED.getStatus();
    }
}
