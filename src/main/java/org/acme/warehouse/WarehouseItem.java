package org.acme.warehouse;

public class WarehouseItem {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityAvailable;
    private String location;
    private Double pricePerUnit;

    public WarehouseItem() {}

    public WarehouseItem(Long id, Long productId, String productName, 
                        Integer quantityAvailable, String location, Double pricePerUnit) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantityAvailable = quantityAvailable;
        this.location = location;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { 
        this.quantityAvailable = quantityAvailable; 
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(Double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public boolean hasEnoughStock(Integer requiredQuantity) {
        return quantityAvailable >= requiredQuantity;
    }

    public void reserveStock(Integer quantity) {
        if (hasEnoughStock(quantity)) {
            quantityAvailable -= quantity;
        } else {
            throw new IllegalStateException("Недостатньо товару на складі");
        }
    }
}
