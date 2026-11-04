package com.example.account.repository;

import com.example.account.entity.TransactionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionLedgerRepository extends JpaRepository<TransactionLedger, Long> {
    List<TransactionLedger> findByAccountId(Long accountId);
}
