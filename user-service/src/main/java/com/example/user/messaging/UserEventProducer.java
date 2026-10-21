package com.example.user.messaging;

import com.example.user.event.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(UserCreatedEvent event) {
        kafkaTemplate.send(
                "user-events",
                String.valueOf(event.userId()),
                event
        );
    }
}
