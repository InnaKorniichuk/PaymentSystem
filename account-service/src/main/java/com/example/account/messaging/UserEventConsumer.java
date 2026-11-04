package com.example.account.messaging;

import com.example.account.entity.KnownUser;
import com.example.account.repository.KnownUserRepository;
import com.example.shared.event.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserEventConsumer {

    private final KnownUserRepository knownUserRepository;

    public UserEventConsumer(KnownUserRepository knownUserRepository) {
        this.knownUserRepository = knownUserRepository;
    }

    @KafkaListener(topics = "user-events")
    public void consume(UserCreatedEvent event){
        KnownUser knownUser = knownUserRepository.findById(event.userId())
                .orElseGet(() -> {
                    KnownUser newUser = new KnownUser();
                    newUser.setUserId(event.userId());

                    return newUser;
                });
        log.info("Received UserCreatedEvent for user ID: {}", knownUser.getUserId());

        knownUser.setActive(true);

        knownUserRepository.save(knownUser);
    }
}
