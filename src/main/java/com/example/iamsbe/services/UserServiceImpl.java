package com.example.iamsbe.services;

import com.example.iamsbe.annotations.LogActivity;
import com.example.iamsbe.exceptions.AppException;
import com.example.iamsbe.exceptions.ErrorCode;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.models.mapper.UserMapper;
import com.example.iamsbe.models.requests.UpdateProfileRequest;
import com.example.iamsbe.models.responses.UserResponse;
import com.example.iamsbe.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Lấy danh sách phân trang
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    @LogActivity("CHANGE_USER_ROLE")
    public UserResponse changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setRole(newRole);
        String currentUsername = SecurityContextHolder
                .getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername) && "ROLE_USER".equals(newRole.toString())) {
            throw new AppException(ErrorCode.CANNOT_CHANGE_ROLE);
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    @LogActivity("DELETE_USER")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        checkDeletePermission(user);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    @LogActivity("CHANGE_USER_STATUS")
    public UserResponse toggleUserStatus(Long userId, boolean isEnabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String currentUsername = SecurityContextHolder
                .getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername) && !isEnabled) {
            throw new AppException(ErrorCode.CANNOT_DISABLE_MYSELF);
        }

        user.setEnabled(isEnabled);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getMyProfile() {
        // 1. Lấy username của người đang login từ Context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm trong DB để lấy đầy đủ thông tin (Họ tên, Role, Provider)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        // 1. Lấy username của người đang đăng nhập từ Security Context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm User trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 3. Cập nhật thông tin
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // 4. Lưu lại và trả về kết quả đã map sang Response
        return userMapper.toResponse(userRepository.save(user));
    }

    private void checkDeletePermission(User targetUser) {
        String currentUsername = SecurityContextHolder
                .getContext().getAuthentication().getName();

        if (targetUser.getUsername().equals(currentUsername)) {
            throw new AppException(ErrorCode.CANNOT_DELETE_MYSELF);
        }
        if (targetUser.getRole() == Role.ROLE_ADMIN) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ADMIN);
        }
    }
}