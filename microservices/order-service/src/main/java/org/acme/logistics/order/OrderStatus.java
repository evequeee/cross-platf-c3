package org.acme.logistics.order;

public enum OrderStatus {
    CREATED,
    VALIDATED,
    WAREHOUSE_RESERVED,
    WAREHOUSE_PROCESSING,
    READY_FOR_DELIVERY,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED,
    FAILED
}
