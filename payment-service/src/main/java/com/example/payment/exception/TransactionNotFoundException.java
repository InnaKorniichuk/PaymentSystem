package com.example.payment.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction not found or access denied for ID: " + id);
    }
}