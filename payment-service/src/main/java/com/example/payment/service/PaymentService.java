package com.example.payment.service;

import com.example.payment.dto.TransferRequest;
import com.example.payment.entity.*;
import com.example.payment.repository.TransactionRepository;
import com.example.shared.event.TransferEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction executeP2PTransfer(TransferRequest request, Long userId) {
        log.info("INITIATE_TRANSFER: Processing payment of {} {} from User [{}] to Recipient User [{}]",
                request.getAmount(), request.getCurrency(), userId, request.getRecipientUserId());

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.debug("Transaction ID [{}] persisted with state: PENDING", savedTransaction.getId());

        TransferEvent event = new TransferEvent(
                savedTransaction.getId(),
                userId,
                request.getRecipientUserId(),
                BigDecimal.valueOf(request.getAmount()),
                request.getCurrency().name()
        );

        kafkaTemplate.send("payment-initiated-topic", event);
        log.info("EVENT_SENT: Transfer event for Transaction ID [{}] published to Kafka", savedTransaction.getId());

        return savedTransaction;
    }

    public Transaction getTransactionById(Long transactionId) {
        log.info("GET_TRANSACTION: Fetching transaction with ID [{}]", transactionId);
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
    }
}