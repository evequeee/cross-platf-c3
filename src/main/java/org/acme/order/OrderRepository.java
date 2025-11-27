package org.acme.order;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class OrderRepository {
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderRepository() {
        // Initialize with sample data
        createSampleOrders();
    }

    private void createSampleOrders() {
        Order order1 = new Order(
            1L,
            "Іван Петренко",
            "ivan@example.com",
            List.of(
                new OrderItem(101L, "Ноутбук", 1, 25000.0),
                new OrderItem(102L, "Миша", 1, 500.0)
            ),
            "Київ, вул. Хрещатик, 1",
            25500.0
        );
        
        Order order2 = new Order(
            2L,
            "Марія Шевченко",
            "maria@example.com",
            List.of(
                new OrderItem(103L, "Телефон", 1, 15000.0)
            ),
            "Львів, пр. Свободи, 10",
            15000.0
        );

        orders.put(1L, order1);
        orders.put(2L, order2);
        idGenerator.set(3L);
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public boolean delete(Long id) {
        return orders.remove(id) != null;
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orders.get(id);
        if (order != null) {
            order.setStatus(status);
        }
        return order;
    }
}
