package com.example.payment.repository;

import com.example.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByDestinationAccountId(Long destinationAccountId);

    List<Transaction> findAllBySourceAccountId(Long sourceAccountId);
}
