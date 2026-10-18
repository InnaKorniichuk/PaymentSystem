package com.example.account.repository;

import com.example.account.entity.Account;
import com.example.account.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);

    Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency);
}
