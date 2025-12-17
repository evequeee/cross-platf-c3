package org.acme.logistics.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_items")
public class WarehouseItem extends PanacheEntity {
    @Column(name = "product_id", unique = true, nullable = false)
    private Long productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private String category;
    
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;
    
    @Column(name = "quantity_reserved", nullable = false)
    private Integer quantityReserved;
    
    @Column(nullable = false)
    private String location;
    
    @Column(name = "warehouse_zone", nullable = false)
    private String warehouseZone;
    
    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit;

    public WarehouseItem() {}

    public WarehouseItem(Long productId, String productName, String category,
                        Integer quantityAvailable, Integer quantityReserved, 
                        String location, String warehouseZone, Double pricePerUnit) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantityAvailable = quantityAvailable;
        this.quantityReserved = quantityReserved;
        this.location = location;
        this.warehouseZone = warehouseZone;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { 
        this.quantityAvailable = quantityAvailable; 
    }

    public Integer getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(Integer quantityReserved) { 
        this.quantityReserved = quantityReserved; 
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWarehouseZone() { return warehouseZone; }
    public void setWarehouseZone(String warehouseZone) { this.warehouseZone = warehouseZone; }

    public Double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(Double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public boolean hasEnoughStock(Integer requiredQuantity) {
        return (quantityAvailable - quantityReserved) >= requiredQuantity;
    }

    public void reserveStock(Integer quantity) {
        if (hasEnoughStock(quantity)) {
            quantityReserved += quantity;
        } else {
            throw new IllegalStateException("Недостатня кількість товару для резервування");
        }
    }

    public void releaseStock(Integer quantity) {
        quantityReserved = Math.max(0, quantityReserved - quantity);
    }

    public void removeStock(Integer quantity) {
        if (quantityAvailable >= quantity) {
            quantityAvailable -= quantity;
            quantityReserved = Math.max(0, quantityReserved - quantity);
        } else {
            throw new IllegalStateException("Недостатня кількість товару на складі");
        }
    }

    public void addStock(Integer quantity) {
        quantityAvailable += quantity;
    }
}
