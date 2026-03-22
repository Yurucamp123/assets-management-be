package com.example.iamsbe.controllers;

import com.example.iamsbe.models.requests.UpdateProfileRequest;
import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.models.responses.UserResponse;
import com.example.iamsbe.services.UserService;
import com.example.iamsbe.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .result(userService.getMyProfile())
                .build());
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse updatedUser = userService.updateMyProfile(request);
        return ResponseUtils.ok(updatedUser, "Cập nhật hồ sơ thành công");
    }
}