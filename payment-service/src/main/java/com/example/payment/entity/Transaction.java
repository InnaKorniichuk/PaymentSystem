package com.example.payment.entity;

import com.example.account.entity.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    private Currency currency;

    private Long sourceAccountId;

    private Long destinationAccountId;

    private LocalDateTime createdAt;
}
