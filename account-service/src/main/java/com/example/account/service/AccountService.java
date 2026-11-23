package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.entity.Currency;
import com.example.account.entity.KnownUser;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.KnownUserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final KnownUserRepository knownUserRepository;

    public AccountService(AccountRepository accountRepository, KnownUserRepository knownUserRepository){
        this.accountRepository = accountRepository;
        this.knownUserRepository = knownUserRepository;
    }

    public Account create(String currency, Long userId){
        KnownUser knownUser = knownUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!knownUser.isActive()) {
            throw new IllegalStateException("User is inactive");
        }

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(0));
        account.setUserId(userId);
        account.setCurrency(Currency.valueOf(currency));

        return accountRepository.save(account);
    }

    public Account findByUserId(Long userId){
        return accountRepository.findByUserId(userId).orElseThrow();
    }

    public Account findByUserIdAndCurrency(Long userId, String currency){
        return accountRepository.findByUserIdAndCurrency(userId, Currency.valueOf(currency)). orElseThrow();
    }
}
