package com.example.user.event;

import java.time.Instant;

public record UserCreatedEvent(
        Long userId,
        Instant createdAt
) {
}