package com.example.notification.service;

import com.example.notification.entity.Notification;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(Long userId, Long transactionId, String status, BigDecimal amount, String currency) {
        log.info("NOTIFICATION_SERVICE: Processing notification for User [{}] regarding Transaction [{}] with status [{}]",
                userId, transactionId, status);

        String message = generateMessage(status, amount, currency);
        NotificationStatus notificationStatus = "COMPLETED".equalsIgnoreCase(status)
                ? NotificationStatus.SENT
                : NotificationStatus.FAILED;

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTransactionId(transactionId);
        notification.setMessage(message);
        notification.setStatus(notificationStatus);
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
        log.info("NOTIFICATION_SAVED: Notification successfully persisted for User [{}]", userId);
    }

    private String generateMessage(String status, BigDecimal amount, String currency) {
        if ("COMPLETED".equalsIgnoreCase(status)) {
            return String.format("Your transfer of %s %s has been successfully completed!", amount, currency);
        }
        return String.format("Unfortunately, an error occurred while processing your transfer of %s %s.", amount, currency);
    }
}