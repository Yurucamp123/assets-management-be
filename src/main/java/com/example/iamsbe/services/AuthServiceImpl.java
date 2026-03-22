package com.example.iamsbe.services;

import com.example.iamsbe.annotations.LogActivity;
import com.example.iamsbe.exceptions.AppException;
import com.example.iamsbe.exceptions.ErrorCode;
import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.models.mapper.UserMapper;
import com.example.iamsbe.models.requests.LoginRequest;
import com.example.iamsbe.models.requests.RegisterRequest;
import com.example.iamsbe.models.responses.UserResponse;
import com.example.iamsbe.repositories.UserRepository;
import com.example.iamsbe.security.jwt.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> authenticateUser(LoginRequest loginRequest) {
        try {
            // 1. Xác thực thông tin người dùng
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Tạo JWT và Cookie
            String jwt = jwtUtils.generateJwtToken(authentication);
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(jwt);

            // 3. Lấy thông tin User trả về
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            Map<String, Object> response = new HashMap<>();
            response.put("cookie", jwtCookie);
            response.put("user", userMapper.toResponse(user));

            return response;

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // Trả về lỗi 401 thay vì lỗi 500 của hệ thống
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Transactional
    @LogActivity("NEW_USER_REGISTER")
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(Role.ROLE_USER);
        user.setProvider("local");

        return userMapper.toResponse(userRepository.save(user));
    }
}
