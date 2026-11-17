package com.example.user.dto;

public record UpdateUserRequest(
        String email,
        String password,
        String currentPassword
) {}