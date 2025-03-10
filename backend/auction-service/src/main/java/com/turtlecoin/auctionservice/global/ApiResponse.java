package com.turtlecoin.auctionservice.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), message, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status.value(), null, data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data, String message) { return new ApiResponse<>(status.value(), message, data); }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), message, null);
    }
}
