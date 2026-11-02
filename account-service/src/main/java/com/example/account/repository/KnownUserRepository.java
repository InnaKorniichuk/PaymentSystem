package com.example.account.repository;

import com.example.account.entity.KnownUser;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;

@Repository
public interface KnownUserRepository extends JpaRepository<KnownUser, Long> {
}