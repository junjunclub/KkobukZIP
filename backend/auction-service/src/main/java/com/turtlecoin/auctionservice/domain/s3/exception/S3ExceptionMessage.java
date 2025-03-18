package com.turtlecoin.auctionservice.domain.s3.exception;

import com.turtlecoin.auctionservice.global.exception.ExceptionMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum S3ExceptionMessage {
    S3_UPLOAD_FAILED("이미지 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
//    INVALID_USER_ROLE("허용되지 않은 유저의 요청입니다.", HttpStatus.FORBIDDEN),
//    USER_ID_DUPLICATED("이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

    S3ExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}