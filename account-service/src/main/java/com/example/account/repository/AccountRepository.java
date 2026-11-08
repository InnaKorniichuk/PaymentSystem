package com.example.account.repository;

import com.example.account.entity.Account;
import com.example.shared.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);

    Optional<Account> findById(Long id);

    Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency);
}
