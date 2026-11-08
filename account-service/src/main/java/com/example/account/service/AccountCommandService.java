package com.example.account.service;

import com.example.account.entity.*;
import com.example.account.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCommandService {

    private final AccountRepository accountRepository;
    private final KnownUserRepository knownUserRepository;
    private final TransactionLedgerRepository ledgerRepository;
    private final IdempotentRequestRepository idempotentRequestRepository;

    @Transactional
    public Account create(com.example.shared.entity.Currency currency, Long userId){
        KnownUser knownUser = knownUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!knownUser.isActive()) {
            throw new IllegalStateException("User is inactive");
        }

        if (accountRepository.findByUserIdAndCurrency(userId, currency).isPresent()) {
            throw new IllegalStateException("An account with this currency already exists for this user.");
        }

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(0));
        account.setUserId(userId);
        account.setCurrency(currency);

        Account savedAccount = accountRepository.save(account);

        ledgerRepository.save(new TransactionLedger(account.getId(), BigDecimal.ZERO, TransactionType.CREATION));

        return savedAccount;
    }

    @Transactional
    public Account deposit(double amount, long userId, com.example.shared.entity.Currency currency, String idempotencyKey){
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            if (idempotentRequestRepository.existsById(idempotencyKey)) {
                log.info("Idempotency key [{}] already processed. Returning existing account state for duplicate request.", idempotencyKey);
                return accountRepository.findByUserIdAndCurrency(userId, currency)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            }
        }

        KnownUser knownUser = knownUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!knownUser.isActive()) {
            throw new IllegalStateException("User is inactive");
        }

        Account account = accountRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new IllegalArgumentException("An account with the currency and userId doesn't exist"));

        BigDecimal prevBalance = account.getBalance();
        account.setBalance(prevBalance.add(BigDecimal.valueOf(amount)));

        ledgerRepository.save(new TransactionLedger(account.getId(), BigDecimal.valueOf(amount), TransactionType.DEPOSIT));

        if(idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotentRequestRepository.save(new IdempotentRequest(idempotencyKey));
            log.info("Successfully processed and stored new idempotency key: [{}]", idempotencyKey);
        }

        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(double amount, long userId, com.example.shared.entity.Currency currency, String idempotencyKey){
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            if (idempotentRequestRepository.existsById(idempotencyKey)) {
                log.info("Idempotency key [{}] already processed. Returning existing account state for duplicate request.", idempotencyKey);
                return accountRepository.findByUserIdAndCurrency(userId, currency)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            }
        }

        KnownUser knownUser = knownUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!knownUser.isActive()) {
            throw new IllegalStateException("User is inactive");
        }

        Account account = accountRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new IllegalArgumentException("An account with the currency and userId doesn't exist"));

        BigDecimal prevBalance = account.getBalance();
        BigDecimal withdrawalAmount = BigDecimal.valueOf(amount);

        if (prevBalance.compareTo(withdrawalAmount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        account.setBalance(prevBalance.subtract(withdrawalAmount));

        ledgerRepository.save(new TransactionLedger(account.getId(), withdrawalAmount, TransactionType.WITHDRAWAL));

        if(idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotentRequestRepository.save(new IdempotentRequest(idempotencyKey));
            log.info("Successfully processed and stored new idempotency key: [{}]", idempotencyKey);
        }

        return accountRepository.save(account);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Account account = accountRepository.findById(id).orElseThrow();

        if(!account.getUserId().equals(userId))
            throw new IllegalStateException("Access denied");

        accountRepository.deleteById(id);
    }
}