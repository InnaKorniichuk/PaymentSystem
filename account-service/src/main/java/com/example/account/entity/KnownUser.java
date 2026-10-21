package com.example.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class KnownUser {
    @Id
    private Long userId;

    private boolean active;

    public KnownUser() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}