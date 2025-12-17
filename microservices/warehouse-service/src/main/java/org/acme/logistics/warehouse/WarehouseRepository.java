package org.acme.logistics.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class WarehouseRepository implements PanacheRepository<WarehouseItem> {

    public List<WarehouseItem> findAllItems() {
        return listAll();
    }

    public Optional<WarehouseItem> findItemById(Long id) {
        return findByIdOptional(id);
    }

    public Optional<WarehouseItem> findByProductId(Long productId) {
        return find("productId", productId).firstResultOptional();
    }

    public List<WarehouseItem> findByCategory(String category) {
        return list("LOWER(category) = LOWER(?1)", category);
    }

    public boolean checkAvailability(Long productId, Integer quantity) {
        return findByProductId(productId)
            .map(item -> item.hasEnoughStock(quantity))
            .orElse(false);
    }

    public void reserveStock(Long productId, Integer quantity) {
        WarehouseItem item = findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Товар не знайдено на складі"));
        item.reserveStock(quantity);
        persist(item);
    }

    public void releaseStock(Long productId, Integer quantity) {
        findByProductId(productId).ifPresent(item -> {
            item.releaseStock(quantity);
            persist(item);
        });
    }

    public void removeStock(Long productId, Integer quantity) {
        WarehouseItem item = findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Товар не знайдено на складі"));
        item.removeStock(quantity);
        persist(item);
    }

    public void addStock(Long productId, Integer quantity) {
        WarehouseItem item = findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Товар не знайдено на складі"));
        item.addStock(quantity);
        persist(item);
    }

}
