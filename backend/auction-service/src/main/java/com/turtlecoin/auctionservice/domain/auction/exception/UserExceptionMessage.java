package com.turtlecoin.auctionservice.domain.auction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionMessage {
    USER_NOT_FOUND("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_ROLE("허용되지 않은 유저의 요청입니다.", HttpStatus.FORBIDDEN),
    USER_ID_DUPLICATED("이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

    UserExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
