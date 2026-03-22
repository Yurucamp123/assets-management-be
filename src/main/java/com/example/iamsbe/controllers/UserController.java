package com.example.iamsbe.controllers;

import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.models.responses.ApiResponse;
import com.example.iamsbe.models.responses.UserResponse;
import com.example.iamsbe.services.UserService;
import com.example.iamsbe.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @ParameterObject Pageable pageable) {
        return ResponseUtils.ok(userService.getAllUsers(pageable), "Lấy danh sách người dùng thành công");
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        return ResponseUtils.ok(userService.changeRole(id, role), "Cập nhật quyền thành công");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam boolean isEnabled) {
        return ResponseUtils.ok(userService.toggleUserStatus(id, isEnabled), "Cập nhật trạng thái thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseUtils.ok(null, "Xóa người dùng thành công");
    }
}