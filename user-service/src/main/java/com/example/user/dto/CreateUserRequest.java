package com.example.user.dto;

public record CreateUserRequest(
        String fullName,
        String email,
        String password
) {}