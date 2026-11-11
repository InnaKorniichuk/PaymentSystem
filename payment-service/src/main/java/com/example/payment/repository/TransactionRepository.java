package com.example.payment.repository;

import com.example.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndSourceAccountId(Long id, Long sourceAccountId);

    List<Transaction> findAllBySourceAccountId(Long sourceAccountId);
}
