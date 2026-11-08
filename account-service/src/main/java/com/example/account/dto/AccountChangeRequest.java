package com.example.account.dto;

import com.example.shared.entity.Currency;

public class AccountChangeRequest {
    private double amount;
    private Currency currency;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}