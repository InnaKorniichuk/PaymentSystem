package com.example.payment.dto;

import com.example.payment.entity.TransactionStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@Builder
public class TransferResponse {
    private Long id;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private double amount;
    private Currency currency;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}