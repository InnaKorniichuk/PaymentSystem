package com.example.payment.repository;

import com.example.payment.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findAllByAccountId(Long accountId);

    List<LedgerEntry> findAllByTransactionId(Long transactionId);
}
