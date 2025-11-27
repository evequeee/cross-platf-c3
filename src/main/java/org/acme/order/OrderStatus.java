package org.acme.order;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    WAREHOUSE_PROCESSING,
    READY_FOR_DELIVERY,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}
