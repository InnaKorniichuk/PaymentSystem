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
import java.util.List;

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
    public ResponseEntity<Transaction> getTransactionStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long currentUserId = Long.valueOf(jwt.getSubject());

        log.info("REST_REQUEST: User [{}] checking status for transaction ID [{}]", currentUserId, id);

        Transaction transaction = paymentService.getTransactionByIdAndUser(id, currentUserId);

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@AuthenticationPrincipal Jwt jwt) {
        Long currentUserId = Long.valueOf(jwt.getSubject());

        log.info("REST_REQUEST: User [{}] requesting transaction history", currentUserId);
        List<Transaction> history = paymentService.getTransactionHistoryForUser(currentUserId);

        return ResponseEntity.ok(history);
    }
}