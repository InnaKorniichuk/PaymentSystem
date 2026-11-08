package com.example.shared.event;

public record PaymentResultEvent(
        Long transactionId,
        String status,
        String reason
) {}