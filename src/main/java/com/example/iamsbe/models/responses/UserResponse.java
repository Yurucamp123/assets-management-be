package com.example.iamsbe.models.responses;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private boolean isEnabled;
}