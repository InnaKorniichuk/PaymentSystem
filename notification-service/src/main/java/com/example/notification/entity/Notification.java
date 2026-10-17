package com.example.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private Long userId;

    private Long transactionId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Instant createdAt;
}