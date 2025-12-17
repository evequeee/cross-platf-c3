package org.acme.logistics.notification;

public enum NotificationStatus {
    PENDING,    // Очікує відправки
    SENT,       // Успішно відправлено
    DELIVERED,  // Доставлено отримувачу
    FAILED,     // Не вдалося відправити
    CANCELLED   // Скасовано
}
