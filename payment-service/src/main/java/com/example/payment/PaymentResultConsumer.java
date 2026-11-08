package com.example.payment;

import com.example.payment.entity.Transaction;
import com.example.payment.entity.TransactionStatus;
import com.example.payment.repository.TransactionRepository;
import com.example.shared.event.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "payment-result-topic", groupId = "payment-service-group")
    @Transactional
    public void consumeResult(PaymentResultEvent event) {
        log.info("Received PaymentResultEvent for transaction ID: [{}] with status: [{}]",
                event.transactionId(), event.status());

        Transaction transaction = transactionRepository.findById(event.transactionId())
                .orElse(null);

        if (transaction != null) {
            if ("COMPLETED".equals(event.status())) {
                transaction.setStatus(TransactionStatus.COMPLETED);
                log.info("TRANSACTION_UPDATED: Transaction ID [{}] marked as COMPLETED", transaction.getId());
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                log.warn("TRANSACTION_UPDATED: Transaction ID [{}] marked as FAILED due to: {}",
                        transaction.getId(), event.reason());
            }
            transactionRepository.save(transaction);
        } else {
            log.error("Transaction not found for ID [{}]", event.transactionId());
        }
    }
}