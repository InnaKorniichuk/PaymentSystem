package com.example.shared.event;

import java.time.Instant;

public record UserCreatedEvent(
        Long userId,
        Instant createdAt
) {}