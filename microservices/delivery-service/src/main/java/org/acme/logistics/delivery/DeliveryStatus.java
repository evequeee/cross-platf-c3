package org.acme.logistics.delivery;

public enum DeliveryStatus {
    PENDING,          // Очікує обробки
    ASSIGNED,         // Призначено водія
    PICKED_UP,        // Товар забрано
    IN_TRANSIT,       // В дорозі
    OUT_FOR_DELIVERY, // На доставці (останній етап)
    DELIVERED,        // Доставлено
    FAILED,           // Не вдалося доставити
    RETURNED          // Повернуто
}
