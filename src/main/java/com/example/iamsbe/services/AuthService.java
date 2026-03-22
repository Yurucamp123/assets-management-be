package com.example.iamsbe.services;

import com.example.iamsbe.models.requests.LoginRequest;
import com.example.iamsbe.models.requests.RegisterRequest;
import com.example.iamsbe.models.responses.UserResponse;
import jakarta.validation.Valid;

import java.util.Map;

public interface AuthService {
    Map<String, Object> authenticateUser(LoginRequest loginRequest);
    UserResponse register(@Valid RegisterRequest registerRequest);
}
