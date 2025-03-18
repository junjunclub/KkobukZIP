package com.turtlecoin.auctionservice.domain.auction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TurtleExceptionMessage {
    TURTLE_NOT_FOUND("해당 거북이가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    TURTLE_ALREADY_REGISTERED("이미 등록된 거북이입니다.", HttpStatus.CONFLICT),
    TURTLE_NOT_OWNED("해당 거북이는 사용자가 소유한 거북이가 아닙니다.", HttpStatus.BAD_REQUEST);
//    INVALID_USER_ROLE("허용되지 않은 유저의 요청입니다.", HttpStatus.FORBIDDEN),
//    USER_ID_DUPLICATED("이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

    TurtleExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
