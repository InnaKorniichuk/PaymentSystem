package com.example.account.controller;

import com.example.account.dto.AccountCreateRequest;
import com.example.account.dto.AccountChangeRequest;import com.example.account.entity.Account;
import com.example.account.service.AccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService=accountService;
    }

    @PostMapping("/create")
    public Account create(@RequestBody AccountCreateRequest request, @AuthenticationPrincipal Jwt jwt){
        return accountService.create(request.getCurrency(), Long.valueOf(jwt.getSubject()));
    }

    @PostMapping("/deposit")
    public Account deposit(@RequestBody AccountChangeRequest request, @AuthenticationPrincipal Jwt jwt){
        return accountService.deposit(request.getAmount(), Long.valueOf(jwt.getSubject()), request.getCurrency());
    }

    @PostMapping("/withdraw")
    public Account withdraw(@RequestBody AccountChangeRequest request, @AuthenticationPrincipal Jwt jwt){
        return accountService.withdraw(request.getAmount(), Long.valueOf(jwt.getSubject()), request.getCurrency());
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        accountService.delete(id,Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/readByUserId")
    public List<Account> readByUserId(@AuthenticationPrincipal Jwt jwt){
        return accountService.findByUserId(Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/read/{id}")
    public Account readById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        return accountService.findById(id, Long.valueOf(jwt.getSubject()));
    }
}
