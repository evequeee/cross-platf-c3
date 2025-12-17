package org.acme.logistics.delivery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.acme.logistics.delivery.client.NotificationClient;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class DeliveryService {

    private static final Logger LOG = Logger.getLogger(DeliveryService.class.getName());
    private static final Random RANDOM = new Random();

    @Inject
    DeliveryRepository deliveryRepository;

    @Inject
    @RestClient
    NotificationClient notificationClient;

    private static final String[] DRIVERS = {
        "Петро Сидоренко:+380671112233:АА 1234 ВВ",
        "Олена Іваненко:+380502223344:АВ 5678 СС",
        "Андрій Коваль:+380933334455:АС 9876 ММ",
        "Марина Бойко:+380674445566:ВВ 1111 КК",
        "Ігор Мельник:+380635556677:СС 2222 ЕЕ"
    };

    public Delivery createDelivery(Long orderId, String deliveryAddress) {
        LOG.info("Створення доставки для замовлення #" + orderId);

        // Вибір випадкового водія
        String[] driverInfo = DRIVERS[RANDOM.nextInt(DRIVERS.length)].split(":");
        
        Delivery delivery = new Delivery(
            null,
            orderId,
            driverInfo[0],
            driverInfo[1],
            driverInfo[2],
            "Склад Центральний, вул. Промислова, 10",
            deliveryAddress,
            LocalDateTime.now().plusHours(3 + RANDOM.nextInt(5))
        );

        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setCurrentLocation("Склад, очікує завантаження");
        
        Delivery saved = deliveryRepository.save(delivery);
        
        LOG.info("Доставку створено з tracking number: " + saved.getTrackingNumber());
        
        return saved;
    }

    public void updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        LOG.info("Оновлення статусу доставки #" + deliveryId + " на " + newStatus);
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RuntimeException("Доставку не знайдено"));

        DeliveryStatus oldStatus = delivery.getStatus();
        deliveryRepository.updateStatus(deliveryId, newStatus);

        // Відправка повідомлення при зміні статусу
        try {
            String message = getStatusChangeMessage(newStatus, delivery);
            notificationClient.sendNotification(
                "customer@example.com", // В реальному додатку - email клієнта з замовлення
                "SMS",
                "Оновлення статусу доставки",
                message
            );
            LOG.info("Повідомлення про зміну статусу відправлено");
        } catch (Exception e) {
            LOG.warning("Не вдалося відправити повідомлення: " + e.getMessage());
        }
    }

    private String getStatusChangeMessage(DeliveryStatus status, Delivery delivery) {
        return switch (status) {
            case ASSIGNED -> "Доставку призначено водію " + delivery.getDriverName() + 
                           ". Tracking: " + delivery.getTrackingNumber();
            case PICKED_UP -> "Товар забрано зі складу. Очікуваний час доставки: " + 
                            delivery.getEstimatedDeliveryTime();
            case IN_TRANSIT -> "Посилка в дорозі. Поточне місцезнаходження: " + 
                             delivery.getCurrentLocation();
            case OUT_FOR_DELIVERY -> "Посилка на фінальній доставці. Водій скоро прибуде.";
            case DELIVERED -> "Посилку успішно доставлено!";
            case FAILED -> "Не вдалося доставити посилку. Зв'яжіться з підтримкою.";
            case RETURNED -> "Посилку повернуто на склад.";
            default -> "Статус доставки оновлено: " + status;
        };
    }
}
