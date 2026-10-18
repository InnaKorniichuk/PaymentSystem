package com.example.notification.repository;

import com.example.notification.entity.Notification;
import com.example.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
     List<Notification> findAllByUserId(Long userId);

    List<Notification> findAllByStatus(NotificationStatus status);
}
