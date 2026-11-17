package com.example.payment.exception;

public class RecipientNotFoundException extends RuntimeException {
    public RecipientNotFoundException(Long id) {
        super("Recipient user not found or inactive: " + id);
    }
}