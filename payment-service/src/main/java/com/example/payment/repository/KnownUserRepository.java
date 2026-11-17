package com.example.payment.repository;

import com.example.payment.entity.KnownUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnownUserRepository extends JpaRepository<KnownUser, Long> {

}