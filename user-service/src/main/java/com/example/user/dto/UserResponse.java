package com.example.user.dto;

import com.example.user.entity.User;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        boolean active
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isActive()
        );
    }
}