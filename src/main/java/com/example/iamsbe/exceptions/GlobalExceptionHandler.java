package com.example.iamsbe.exceptions;

import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Lỗi hệ thống chưa xác định (500)
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(Exception exception) {
        // Dùng ErrorCode đã định nghĩa và nạp vào Util
        return ResponseUtils.error(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }

    // 2. Lỗi nghiệp vụ (AppException) - Trả về đúng mã lỗi đã throw
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        return ResponseUtils.error(exception.getErrorCode());
    }

    // 3. Lỗi Validation dữ liệu đầu vào
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getBindingResult().getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
        }
        return ResponseUtils.error(errorCode);
    }

    // 4. Lỗi phân quyền (403 Forbidden)
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException() {
        return ResponseUtils.error(ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        String message = "Định dạng dữ liệu không hợp lệ. ";
        if (ex.getMessage().contains("java.time.LocalDate")) {
            message += "Ngày tháng không tồn tại hoặc sai định dạng (yyyy-MM-dd).";
        }

        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(400)
                .message(message)
                .build());
    }
}