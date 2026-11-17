package com.example.payment.messaging;

import com.example.payment.entity.KnownUser;
import com.example.payment.repository.KnownUserRepository;
import com.example.shared.event.UserCreatedEvent;
import com.example.shared.event.UserDeactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@KafkaListener(topics = "user-events", groupId = "payment-service-group")
public class UserEventConsumer {

    private final KnownUserRepository knownUserRepository;

    @KafkaHandler
    public void onCreated(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent for user ID: {}", event.userId());
        if (knownUserRepository.existsById(event.userId())) {
            return;
        }
        KnownUser user = new KnownUser();
        user.setUserId(event.userId());
        user.setActive(true);
        knownUserRepository.save(user);
    }

    @KafkaHandler
    public void onDeactivated(UserDeactivatedEvent event) {
        log.info("Received UserDeactivatedEvent for user ID: {}", event.userId());
        KnownUser user = knownUserRepository.findById(event.userId()).orElseGet(() -> {
            KnownUser n = new KnownUser();
            n.setUserId(event.userId());
            return n;
        });
        user.setActive(false);
        knownUserRepository.save(user);
    }

    @KafkaHandler(isDefault = true)
    public void onUnknown(Object event) {
        log.warn("Ignoring unknown event: {}", event);
    }
}