package com.example.iamsbe.services;

import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.models.requests.UpdateProfileRequest;
import com.example.iamsbe.models.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse changeRole(Long userId, Role newRole);
    void deleteUser(Long userId);
    UserResponse toggleUserStatus(Long userId, boolean isEnabled);
    UserResponse getMyProfile();
    UserResponse updateMyProfile(UpdateProfileRequest request);
}
