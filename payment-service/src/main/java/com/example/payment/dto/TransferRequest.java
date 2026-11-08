package com.example.payment.dto;

import lombok.Data;
import com.example.shared.entity.Currency;

@Data
public class TransferRequest {
  private Long recipientUserId;
  private double amount;
  private Currency currency;
  private String idempotencyKey;
}