package com.example.iamsbe.utils;

import com.example.iamsbe.exceptions.ErrorCode;
import com.example.iamsbe.models.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseUtils {

    // Trả về 200 OK kèm dữ liệu
    public static <T> ResponseEntity<ApiResponse<T>> ok(T result, String message) {
        return ResponseEntity.ok(ApiResponse.success(200, message, result));
    }

    // Trả về 201 Created khi thêm mới thành công
    public static <T> ResponseEntity<ApiResponse<T>> created(T result, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, message, result));
    }

    // Trả về 204 No Content khi xóa thành công
    public static ResponseEntity<ApiResponse<Void>> noContent(String message) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(204, message, null));
    }

    // Trả về lỗi tùy chỉnh dựa trên ErrorCode
    public static ResponseEntity<ApiResponse<Object>> error(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .statusCode(errorCode.getStatusCode().value())
                        .message(errorCode.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}