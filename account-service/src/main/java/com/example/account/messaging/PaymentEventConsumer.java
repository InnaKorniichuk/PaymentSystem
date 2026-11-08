package com.example.account.messaging;

import com.example.account.entity.Account;
import com.example.shared.entity.Currency;
import com.example.account.repository.AccountRepository;
import com.example.shared.event.TransferEvent;
import com.example.shared.event.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, PaymentResultEvent> kafkaTemplate; // Додаємо KafkaTemplate для зворотного зв'язку

    @KafkaListener(topics = "payment-initiated-topic", groupId = "account-service-group")
    @Transactional
    public void consume(TransferEvent event) {
        log.info("Received TransferEvent for transaction ID: {} from user {} with currency {}",
                event.transactionId(), event.userId(), event.currency());

        try {
            Account sourceAccount = accountRepository.findByUserIdAndCurrency(
                    event.userId(),
                    Currency.valueOf(event.currency())
            ).orElseThrow(() -> new IllegalArgumentException(
                    "Source account not found for user: " + event.userId()));

            if (sourceAccount.getBalance().compareTo(event.amount()) < 0) {
                throw new IllegalStateException("Insufficient funds in source account");
            }

            Account destinationAccount = accountRepository.findByUserIdAndCurrency(
                    event.recipientUserId(),
                    Currency.valueOf(event.currency())
            ).orElseThrow(() -> new IllegalArgumentException(
                    "Destination account not found for recipient user: " + event.recipientUserId()));

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(event.amount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(event.amount()));

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            log.info("TRANSFER_PROCESSED_SUCCESSFULLY: Transaction ID [{}]", event.transactionId());

            PaymentResultEvent resultEvent = new PaymentResultEvent(event.transactionId(), "COMPLETED", null);
            kafkaTemplate.send("payment-result-topic", resultEvent);

        } catch (Exception e) {
            log.error("TRANSFER_PROCESSING_FAILED: Transaction ID [{}] due to: {}", event.transactionId(), e.getMessage());

            PaymentResultEvent resultEvent = new PaymentResultEvent(event.transactionId(), "FAILED", e.getMessage());
            kafkaTemplate.send("payment-result-topic", resultEvent);
        }
    }
}