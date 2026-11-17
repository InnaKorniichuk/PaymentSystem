package com.example.payment.messaging;

import com.example.payment.entity.KnownUser;
import com.example.payment.repository.KnownUserRepository;
import com.example.shared.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final KnownUserRepository knownUserRepository;

    @KafkaListener(topics = "user-events", groupId = "payment-service-group")
    public void consume(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent for user ID: {}", event.userId());
        knownUserRepository.save(new KnownUser(event.userId(), true));
    }
}