package com.example.notification.messaging;

import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import com.example.shared.event.PaymentResultEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "payment-result-topic", groupId = "notification-service-group")
    public void consumePaymentResult(PaymentResultEvent event) {
        log.info("KAFKA_CONSUMER: Received payment result event for Transaction ID [{}] with status [{}]",
                event.transactionId(), event.status());

        notificationService.createNotification(
                event.userId(),
                event.transactionId(),
                event.status(),
                event.amount(),
                event.currency()
        );
    }
}