package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.entity.Currency;
import com.example.account.entity.KnownUser;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.KnownUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final KnownUserRepository knownUserRepository;

    public AccountService(AccountRepository accountRepository, KnownUserRepository knownUserRepository){
        this.accountRepository = accountRepository;
        this.knownUserRepository = knownUserRepository;
    }

    public Account create(Currency currency, Long userId){
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

        return accountRepository.save(account);
    }

    @Transactional
    public Account deposit(double amount, long userId, Currency currency){
        KnownUser knownUser = knownUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!knownUser.isActive()) {
            throw new IllegalStateException("User is inactive");
        }

        Account account = accountRepository.findByUserIdAndCurrency(userId, currency).orElseThrow(() ->  new IllegalArgumentException ("An account with the currency and userId doesn't exist"));

        BigDecimal prevBalance = account.getBalance();

        account.setBalance(prevBalance.add(BigDecimal.valueOf(amount)));

        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(double amount, long userId, Currency currency){
        KnownUser knownUser = knownUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!knownUser.isActive()) {
            throw new IllegalStateException("User is inactive");
        }

        Account account = accountRepository.findByUserIdAndCurrency(userId, currency).orElseThrow(() ->  new IllegalArgumentException ("An account with the currency and userId doesn't exist"));

        BigDecimal prevBalance = account.getBalance();

        account.setBalance(prevBalance.subtract(BigDecimal.valueOf(amount)));

        return accountRepository.save(account);
    }

    public List<Account> findByUserId(Long userId){
        return accountRepository.findByUserId(userId);
    }

    public Account findById(Long id, Long userId){
        Account account = accountRepository.findById(id).orElseThrow();

        if(!account.getUserId().equals(userId))
            throw new IllegalStateException("Access denied");

        return account;
    }

    public void delete(Long id, Long userId) {
        Account account = accountRepository.findById(id).orElseThrow();

        if(!account.getUserId().equals(userId))
            throw new IllegalStateException("Access denied");

        accountRepository.deleteById(id);
    }

    public Account findByUserIdAndCurrency(Long userId, String currency){
        return accountRepository.findByUserIdAndCurrency(userId, Currency.valueOf(currency)). orElseThrow();
    }
}
