package org.acme.warehouse;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WarehouseRepository {
    private final Map<Long, WarehouseItem> items = new ConcurrentHashMap<>();
    private final Map<Long, WarehouseItem> itemsByProductId = new ConcurrentHashMap<>();

    public WarehouseRepository() {
        createSampleItems();
    }

    private void createSampleItems() {
        WarehouseItem item1 = new WarehouseItem(
            1L, 101L, "Ноутбук", 50, "A-12-3", 25000.0
        );
        WarehouseItem item2 = new WarehouseItem(
            2L, 102L, "Миша", 200, "B-05-1", 500.0
        );
        WarehouseItem item3 = new WarehouseItem(
            3L, 103L, "Телефон", 100, "A-15-7", 15000.0
        );
        WarehouseItem item4 = new WarehouseItem(
            4L, 104L, "Клавіатура", 150, "B-08-2", 1200.0
        );

        items.put(1L, item1);
        items.put(2L, item2);
        items.put(3L, item3);
        items.put(4L, item4);

        itemsByProductId.put(101L, item1);
        itemsByProductId.put(102L, item2);
        itemsByProductId.put(103L, item3);
        itemsByProductId.put(104L, item4);
    }

    public List<WarehouseItem> findAll() {
        return new ArrayList<>(items.values());
    }

    public Optional<WarehouseItem> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Optional<WarehouseItem> findByProductId(Long productId) {
        return Optional.ofNullable(itemsByProductId.get(productId));
    }

    public boolean checkAvailability(Long productId, Integer quantity) {
        return findByProductId(productId)
            .map(item -> item.hasEnoughStock(quantity))
            .orElse(false);
    }

    public void reserveStock(Long productId, Integer quantity) {
        findByProductId(productId).ifPresent(item -> item.reserveStock(quantity));
    }
}
