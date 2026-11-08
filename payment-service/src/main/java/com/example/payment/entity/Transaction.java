package com.example.payment.entity;

import com.example.shared.entity.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Long sourceAccountId;

    private Long destinationAccountId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime createdAt;
}
