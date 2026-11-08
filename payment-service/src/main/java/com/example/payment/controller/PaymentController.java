package com.example.payment.controller;

import com.example.payment.dto.TransferRequest;
import com.example.payment.entity.Transaction;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(
            @RequestBody TransferRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long sourceAccountId = Long.valueOf(jwt.getSubject());

        log.info("REST_REQUEST: User/Account [{}] initiating transfer to user [{}]",
                sourceAccountId, request.getRecipientUserId());

        Transaction completedTransaction = paymentService.executeP2PTransfer(request, sourceAccountId);

        return ResponseEntity.ok(completedTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionStatus(@PathVariable Long id) {
        log.info("REST_REQUEST: Checking status for transaction ID [{}]", id);
        Transaction transaction = paymentService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
}