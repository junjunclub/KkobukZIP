package com.turtlecoin.auctionservice.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonExceptionMessage implements ExceptionMessage {
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

    CommonExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
