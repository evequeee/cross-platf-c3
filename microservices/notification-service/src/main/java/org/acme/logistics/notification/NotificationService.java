package org.acme.logistics.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@ApplicationScoped
public class NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationService.class.getName());
    private static final int MAX_RETRY_COUNT = 3;

    @Inject
    NotificationRepository notificationRepository;

    public Notification sendNotification(String recipient, NotificationType type, 
                                        String subject, String message) {
        LOG.info("Відправка " + type + " повідомлення до: " + recipient);

        Notification notification = new Notification(
            recipient, type, subject, message
        );
        
        notification = notificationRepository.save(notification);

        // Симуляція відправки повідомлення
        try {
            boolean sent = simulateSending(type, recipient);
            
            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                LOG.info("Повідомлення #" + notification.getId() + " успішно відправлено");
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Помилка відправки");
                LOG.warning("Не вдалося відправити повідомлення #" + notification.getId());
            }
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            LOG.severe("Помилка при відправці: " + e.getMessage());
        }

        return notificationRepository.save(notification);
    }

    public void retryNotification(Long notificationId) {
        LOG.info("Повторна спроба відправки повідомлення #" + notificationId);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Повідомлення не знайдено"));

        if (notification.getRetryCount() >= MAX_RETRY_COUNT) {
            throw new RuntimeException("Перевищено максимальну кількість спроб відправки");
        }

        notification.setRetryCount(notification.getRetryCount() + 1);

        try {
            boolean sent = simulateSending(notification.getType(), notification.getRecipient());
            
            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                LOG.info("Повідомлення успішно відправлено після " + notification.getRetryCount() + " спроб");
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Помилка відправки (спроба " + notification.getRetryCount() + ")");
            }
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }

        notificationRepository.save(notification);
    }

    private boolean simulateSending(NotificationType type, String recipient) {
        // Симуляція відправки різних типів повідомлень
        LOG.info("Симуляція відправки " + type + " повідомлення...");

        // Для демонстрації: невалідні адреси призводять до помилки
        if (recipient.equals("invalid@email") || recipient.isEmpty()) {
            return false;
        }

        switch (type) {
            case EMAIL:
                LOG.info("📧 Email відправлено на: " + recipient);
                return true;
            case SMS:
                LOG.info("📱 SMS відправлено на: " + recipient);
                return true;
            case PUSH:
                LOG.info("🔔 Push notification відправлено для: " + recipient);
                return true;
            case IN_APP:
                LOG.info("💬 In-app повідомлення створено для: " + recipient);
                return true;
            default:
                return false;
        }
    }

    public void processFailedNotifications() {
        LOG.info("Обробка невдалих повідомлень...");
        
        var failedNotifications = notificationRepository.findFailedWithRetries();
        
        for (Notification notification : failedNotifications) {
            try {
                retryNotification(notification.getId());
            } catch (Exception e) {
                LOG.warning("Не вдалося повторно відправити повідомлення #" + 
                          notification.getId() + ": " + e.getMessage());
            }
        }
    }
}
