package com.turtlecoin.auctionservice.domain.auction.exception;

import com.turtlecoin.auctionservice.global.exception.ExceptionMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuctionExceptionMessage {
    AUCTION_NOT_FOUND("해당 경매가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PHOTO_NOT_UPLOADED("이미지를 최소 한 개 이상 업로드해야 합니다.", HttpStatus.BAD_REQUEST),
    AUCTION_TIME_NOT_VALID("유효하지 않은 경매 시간입니다.", HttpStatus.BAD_REQUEST),
    AUCTION_ALREADY_FINISHED("이미 경매가 종료됐습니다.", HttpStatus.BAD_REQUEST),
    PHOTO_UPLOAD_FAILED("이미지 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),;
//    INVALID_USER_ROLE("허용되지 않은 유저의 요청입니다.", HttpStatus.FORBIDDEN),
//    USER_ID_DUPLICATED("이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

    AuctionExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
