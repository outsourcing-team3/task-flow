package com.example.outsourcingproject.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    // 성공 응답 생성자
    private ApiResponse(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 실패 응답 생성자 1
    private ApiResponse(String message) {
        this.success = false;
        this.message = message;
        this.data = null;
        this.timestamp = LocalDateTime.now();
    }

    // 실패 응답 생성자 2
    private ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    // 성공 - 데이터 있음
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "요청이 성공적으로 처리되었습니다.");
    }

    // 성공 - 데이터 있음 + 커스텀 메시지
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    // 성공 - 데이터 없음 (예: 삭제, 업데이트)
    public static <Void> ApiResponse<Void> success() {
        return new ApiResponse<>(null, "요청이 성공적으로 처리되었습니다.");
    }

    // 성공 - 데이터 없음 + 커스텀 메시지
    public static <Void> ApiResponse<Void> success(String message) {
        return new ApiResponse<>(null, message);
    }

    // 실패
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(message);
    }

    // 실패 - 데이터 포함 (RateLimitException용)
    public static <T> ApiResponse<T> failure(String message, T data) {
        return new ApiResponse<>(false, message, data, LocalDateTime.now());
    }
}