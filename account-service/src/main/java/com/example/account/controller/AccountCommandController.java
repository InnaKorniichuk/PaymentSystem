package com.example.account.controller;

import com.example.account.dto.AccountChangeRequest;
import com.example.account.dto.AccountCreateRequest;
import com.example.account.entity.Account;
import com.example.account.service.AccountCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountCommandController {

    private final AccountCommandService commandService;

    @PostMapping("/create")
    public Account create(@RequestBody AccountCreateRequest request, @AuthenticationPrincipal Jwt jwt){
        return commandService.create(request.getCurrency(), Long.valueOf(jwt.getSubject()));
    }

    @PostMapping("/deposit")
    public Account deposit(@RequestBody AccountChangeRequest request,
                           @AuthenticationPrincipal Jwt jwt,
                           @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey){
        return commandService.deposit(request.getAmount(), Long.valueOf(jwt.getSubject()), request.getCurrency(), idempotencyKey);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Account> withdraw(@RequestBody AccountChangeRequest request,
                                            @AuthenticationPrincipal Jwt jwt,
                                            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey){
        Account updatedAccount = commandService.withdraw(request.getAmount(), Long.valueOf(jwt.getSubject()), request.getCurrency(), idempotencyKey);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        commandService.delete(id, Long.valueOf(jwt.getSubject()));
    }
}