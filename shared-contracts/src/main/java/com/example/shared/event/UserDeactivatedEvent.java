package com.example.shared.event;

import java.time.Instant;

public record UserDeactivatedEvent(
        Long userId,
        Instant deactivatedAt)
{}