package com.example.iamsbe.models.entities;

import com.example.iamsbe.models.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "USERNAME_REQUIRED")
    @Column(unique = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @NotBlank(message = "FULLNAME_REQUIRED")
    private String fullName;

    @Email(message = "EMAIL_INVALID")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;

    @Column(name = "is_enabled")
    private boolean isEnabled = true;
}