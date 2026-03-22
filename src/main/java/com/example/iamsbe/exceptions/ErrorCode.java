package com.example.iamsbe.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 9xxx - System Errors
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống chưa xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9001, "Mã lỗi không hợp lệ", HttpStatus.BAD_REQUEST),

    // 1xxx - Auth & User Errors
    USER_EXISTED(1001, "Người dùng đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1003, "Email đã được sử dụng", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "Vui lòng đăng nhập để tiếp tục", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1006, "Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    USER_DISABLED(1007, "Tài khoản người dùng này đã bị khóa", HttpStatus.BAD_REQUEST),

    CANNOT_DELETE_MYSELF(1101, "Bạn không thể tự xóa tài khoản của chính mình", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_ADMIN(1102, "Không được phép xóa tài khoản quản trị viên", HttpStatus.FORBIDDEN),
    CANNOT_DISABLE_MYSELF(1103, "Bạn không thể tự khóa tài khoản của chính mình", HttpStatus.BAD_REQUEST),
    CANNOT_CHANGE_ROLE(1104, "Bạn không thể tự thay đổi role của chính mình", HttpStatus.BAD_REQUEST),

    // 12xx - Validation Errors (Bean Validation)
    USERNAME_REQUIRED(1201, "Tên đăng nhập không được để trống", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID_SIZE(1202, "Tên đăng nhập phải từ 3 đến 20 ký tự", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1203, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(1204, "Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1205, "Email không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1206, "Định dạng email không hợp lệ", HttpStatus.BAD_REQUEST),
    FULLNAME_REQUIRED(1207, "Họ và tên không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1208, "Dữ liệu yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    // 2xxx - Asset Errors
    ASSET_NOT_FOUND(2001, "Không tìm thấy thiết bị này trong kho", HttpStatus.NOT_FOUND),
    ASSET_ALREADY_ASSIGNED(2002, "Thiết bị này hiện đã có người mượn", HttpStatus.BAD_REQUEST),
    ASSET_NAME_REQUIRED(2003, "Tên thiết bị không được để trống", HttpStatus.BAD_REQUEST),
    ASSET_TAG_REQUIRED(2004, "Mã thẻ tài sản (Asset Tag) không được để trống", HttpStatus.BAD_REQUEST),
    ASSET_TAG_DUPLICATE(2005, "Mã thẻ tài sản này đã tồn tại", HttpStatus.BAD_REQUEST),
    ASSET_ALREADY_ASSIGNED_ADMIN(2006, "Máy hiện đang có người sử dụng, vui lòng duyệt sau khi máy được trả.",
            HttpStatus.BAD_REQUEST),

    // 3xxx - Request Errors
    REQUEST_NOT_FOUND(3001, "Không tìm thấy yêu cầu mượn/trả này", HttpStatus.NOT_FOUND),
    BORROW_DATE_INVALID(3002, "Ngày mượn không được là ngày trong quá khứ", HttpStatus.BAD_REQUEST),
    RETURN_DATE_BEFORE_BORROW(3003, "Ngày trả không được trước ngày mượn", HttpStatus.BAD_REQUEST),
    REQUEST_ALREADY_EXISTS(3004, "Thiết bị này đã có yêu cầu mượn", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}