package com.example.user.service;

import com.example.shared.event.UserDeactivatedEvent;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.shared.event.UserCreatedEvent;
import com.example.user.messaging.UserEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;
    private final JwtService jwtService;

    public User create(String fullName, String email, String password) {
        if (isBlank(fullName) || isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("fullName, email and password are required");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already in use");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User saved = userRepository.save(user);
        userEventProducer.sendMessage(new UserCreatedEvent(saved.getId(), Instant.now()));
        return saved;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isActive() || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return jwtService.generateToken(user.getId(), user.getEmail());
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    @Transactional
    public User update(Long userId, String email, String password, String currentPassword) {
        User user = getById(userId);

        if (isBlank(email) && isBlank(password)) {
            throw new IllegalArgumentException("Nothing to update");
        }
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!isBlank(email) && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalStateException("Email already in use");
            }
            user.setEmail(email);
        }
        if (!isBlank(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deactivate(Long userId) {
        User user = getById(userId);
        if (!user.isActive()) return;
        user.setActive(false);
        userRepository.save(user);
        userEventProducer.sendMessage(new UserDeactivatedEvent(userId, Instant.now()));
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}