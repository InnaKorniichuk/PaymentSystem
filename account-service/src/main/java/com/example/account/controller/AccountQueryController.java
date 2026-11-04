package com.example.account.controller;

import com.example.account.entity.Account;
import com.example.account.entity.TransactionLedger;
import com.example.account.service.AccountQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountQueryController {

    private final AccountQueryService queryService;
    private final AccountQueryService accountQueryService;

    @GetMapping("/readByUserId")
    public List<Account> readByUserId(@AuthenticationPrincipal Jwt jwt){
        return queryService.findByUserId(Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/read/{id}")
    public Account readById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        return queryService.findById(id, Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/{accountId}/history")
    public ResponseEntity<List<TransactionLedger>> getAccountHistory(@PathVariable Long accountId,  @AuthenticationPrincipal Jwt jwt) {
        List<TransactionLedger> history = accountQueryService.getAccountHistory(accountId, Long.valueOf(jwt.getSubject()));
        return ResponseEntity.ok(history);
    }
}