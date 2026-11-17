package com.example.user.controller;

import com.example.user.dto.*;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        var user = userService.create(request.fullName(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return new LoginResponse(userService.login(request.email(), request.password()));
    }

    @GetMapping("/profile")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(userService.getById(Long.valueOf(jwt.getSubject())));
    }

    @PatchMapping("/update")
    public UserResponse update(@AuthenticationPrincipal Jwt jwt,
                               @RequestBody UpdateUserRequest request) {
        var user = userService.update(
                Long.valueOf(jwt.getSubject()),
                request.email(),
                request.password(),
                request.currentPassword());
        return UserResponse.from(user);
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<Void> deactivate(@AuthenticationPrincipal Jwt jwt) {
        userService.deactivate(Long.valueOf(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}