package com.example.iamsbe.models.mapper;

import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.models.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserResponse toResponse(User user);

    default String mapRoleToString(Role role) {
        if (role == null) return null;
        return role.name(); // Trả về "ADMIN" hoặc "USER"
    }
}