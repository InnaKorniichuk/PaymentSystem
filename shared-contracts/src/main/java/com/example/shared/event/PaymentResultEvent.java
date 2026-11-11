package com.example.shared.event;

import java.math.BigDecimal;

public record PaymentResultEvent(
        Long transactionId,
        Long userId,
        String status,
        String reason,
        BigDecimal amount,
        String currency
) {}