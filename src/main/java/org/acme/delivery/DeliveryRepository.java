package org.acme.delivery;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class DeliveryRepository {
    private final Map<Long, Delivery> deliveries = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public DeliveryRepository() {
        createSampleDeliveries();
    }

    private void createSampleDeliveries() {
        Delivery delivery1 = new Delivery(
            1L,
            1L,
            "Олександр Водій",
            "+380501234567",
            "Склад №1, Київ",
            "Київ, вул. Хрещатик, 1",
            LocalDateTime.now().plusHours(3)
        );
        delivery1.setStatus(DeliveryStatus.IN_TRANSIT);

        Delivery delivery2 = new Delivery(
            2L,
            2L,
            "Петро Курьєр",
            "+380502345678",
            "Склад №2, Львів",
            "Львів, пр. Свободи, 10",
            LocalDateTime.now().plusHours(5)
        );
        delivery2.setStatus(DeliveryStatus.PENDING);

        deliveries.put(1L, delivery1);
        deliveries.put(2L, delivery2);
        idGenerator.set(3L);
    }

    public List<Delivery> findAll() {
        return new ArrayList<>(deliveries.values());
    }

    public Optional<Delivery> findById(Long id) {
        return Optional.ofNullable(deliveries.get(id));
    }

    public Optional<Delivery> findByOrderId(Long orderId) {
        return deliveries.values().stream()
            .filter(d -> d.getOrderId().equals(orderId))
            .findFirst();
    }

    public Delivery save(Delivery delivery) {
        if (delivery.getId() == null) {
            delivery.setId(idGenerator.getAndIncrement());
        }
        deliveries.put(delivery.getId(), delivery);
        return delivery;
    }

    public Delivery updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveries.get(id);
        if (delivery != null) {
            delivery.setStatus(status);
            if (status == DeliveryStatus.DELIVERED) {
                delivery.setActualDeliveryTime(LocalDateTime.now());
            }
        }
        return delivery;
    }
}
