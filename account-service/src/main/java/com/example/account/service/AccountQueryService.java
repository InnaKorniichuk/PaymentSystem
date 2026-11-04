package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.entity.Currency;
import com.example.account.entity.TransactionLedger;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.TransactionLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountQueryService {

    private final AccountRepository accountRepository;
    private final TransactionLedgerRepository ledgerRepository;

    public List<Account> findByUserId(Long userId){
        return accountRepository.findByUserId(userId);
    }

    public Account findById(Long id, Long userId){
        Account account = accountRepository.findById(id).orElseThrow();

        if(!account.getUserId().equals(userId))
            throw new IllegalStateException("Access denied");

        return account;
    }

    public Account findByUserIdAndCurrency(Long userId, String currency){
        return accountRepository.findByUserIdAndCurrency(userId, Currency.valueOf(currency))
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Transactional(readOnly = true)
    public List<TransactionLedger> getAccountHistory(Long accountId, Long authenticatedUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found with ID: " + accountId));

        if (!account.getUserId().equals(authenticatedUserId)) {
            throw new IllegalStateException("Unauthorized: You do not own this account");
        }

        return ledgerRepository.findByAccountId(accountId);
    }
}