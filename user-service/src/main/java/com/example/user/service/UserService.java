package com.example.user.service;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.event.UserCreatedEvent;
import com.example.user.messaging.UserEventProducer;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserEventProducer userEventProducer,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEventProducer = userEventProducer;
        this.jwtService = jwtService;
    }

    public User create(String fullName, String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        UserCreatedEvent event =
                new UserCreatedEvent(savedUser.getId(), Instant.now());

        userEventProducer.sendMessage(event);
        return savedUser;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(), user.getEmail());

        return new LoginResponse(token);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}