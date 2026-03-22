package com.example.iamsbe.controllers;

import com.example.iamsbe.models.requests.LoginRequest;
import com.example.iamsbe.models.requests.RegisterRequest;
import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.models.responses.UserResponse;
import com.example.iamsbe.security.jwt.JwtUtils;
import com.example.iamsbe.services.AuthService;
import com.example.iamsbe.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        UserResponse userResponse = authService.register(registerRequest);
        return ResponseUtils.ok(userResponse, "Đăng ký tài khoản thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        
        Map<String, Object> authData = authService.authenticateUser(loginRequest);
        
        // Trích xuất cookie và user từ map
        ResponseCookie jwtCookie = (ResponseCookie) authData.get("cookie");
        UserResponse userResponse = (UserResponse) authData.get("user");

        // Đính kèm cookie vào header
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseUtils.ok(userResponse, "Đăng nhập thành công");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseUtils.ok(null, "Đã đăng xuất");
    }

    @GetMapping("/social-set-cookie")
    public ResponseEntity<?> socialSetCookie(@RequestParam String token) {
        // Validate xem token gửi qua URL có "xịn" không
        if (jwtUtils.validateJwtToken(token)) {
            // Tạo Cookie HttpOnly từ token đó
            ResponseCookie cookie = jwtUtils.generateJwtCookie(token);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("message", "Đã set Cookie an toàn cho Social Login!"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ!");
    }
}