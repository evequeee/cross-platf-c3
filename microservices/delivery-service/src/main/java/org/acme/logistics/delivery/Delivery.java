package org.acme.logistics.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", unique = true, nullable = false)
    private Long orderId;
    
    @Column(name = "driver_name")
    private String driverName;
    
    @Column(name = "driver_phone")
    private String driverPhone;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    
    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;
    
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;
    
    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;
    
    @Column(name = "tracking_number", unique = true, nullable = false)
    private String trackingNumber;
    
    @Column(name = "current_location")
    private String currentLocation;

    public Delivery() {
        this.status = DeliveryStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.trackingNumber = generateTrackingNumber();
    }

    public Delivery(Long orderId, String driverName, String driverPhone,
                    String vehicleNumber, String pickupAddress, String deliveryAddress,
                    LocalDateTime estimatedDeliveryTime) {
        this();
        this.orderId = orderId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.vehicleNumber = vehicleNumber;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (trackingNumber == null) {
            trackingNumber = generateTrackingNumber();
        }
        if (status == null) {
            status = DeliveryStatus.PENDING;
        }
    }

    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { 
        this.estimatedDeliveryTime = estimatedDeliveryTime; 
    }

    public LocalDateTime getActualDeliveryTime() { return actualDeliveryTime; }
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) { 
        this.actualDeliveryTime = actualDeliveryTime; 
    }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
}
