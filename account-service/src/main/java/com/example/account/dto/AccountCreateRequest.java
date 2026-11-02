package com.example.account.dto;

import com.example.account.entity.Currency;

public class AccountCreateRequest {
    private Currency currency;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}