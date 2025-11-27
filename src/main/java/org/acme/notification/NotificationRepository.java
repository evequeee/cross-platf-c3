package org.acme.notification;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class NotificationRepository {
    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notifications.get(id));
    }

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idGenerator.getAndIncrement());
        }
        notifications.put(notification.getId(), notification);
        return notification;
    }

    public Notification send(Notification notification) {
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        return save(notification);
    }

    public List<Notification> findByRecipient(String recipient) {
        return notifications.values().stream()
            .filter(n -> n.getRecipient().equals(recipient))
            .toList();
    }
}
