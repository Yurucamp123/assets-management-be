package com.example.iamsbe.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T result;
    private LocalDateTime timestamp;
    private int statusCode;

    public static <T> ApiResponse<T> success(int code, String message, T result) {
        return ApiResponse.<T>builder()
                .statusCode(code)
                .code(null)
                .message(message)
                .result(result)
                .timestamp(LocalDateTime.now())
                .build();
    }
}