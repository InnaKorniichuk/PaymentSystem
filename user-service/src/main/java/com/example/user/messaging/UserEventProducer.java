package com.example.user.messaging;

import com.example.shared.event.UserCreatedEvent;
import com.example.shared.event.UserDeactivatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(UserCreatedEvent event) {
        send(event.userId(), event);
    }

    public void sendMessage(UserDeactivatedEvent event) {
        send(event.userId(), event);
    }

    private void send(Long userId, Object event) {
        kafkaTemplate.send("user-events", String.valueOf(userId), event);
    }
}