package com.example.account.repository;

import com.example.account.entity.IdempotentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotentRequestRepository extends JpaRepository<IdempotentRequest, String> {
}