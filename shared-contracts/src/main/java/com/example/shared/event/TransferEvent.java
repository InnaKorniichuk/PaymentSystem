package com.example.shared.event;

import java.math.BigDecimal;

public record TransferEvent(
        Long transactionId,
        Long userId,
        Long recipientUserId,
        BigDecimal amount,
        String currency
) {}