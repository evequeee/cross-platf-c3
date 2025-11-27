package org.acme.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.acme.warehouse.WarehouseClient;
import org.acme.delivery.DeliveryClient;
import org.acme.notification.NotificationClient;
import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    @RestClient
    WarehouseClient warehouseClient;

    @Inject
    @RestClient
    DeliveryClient deliveryClient;

    @Inject
    @RestClient
    NotificationClient notificationClient;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order createOrder(Order order) {
        // Validate and save order
        Order savedOrder = orderRepository.save(order);
        
        // Send notification about new order
        try {
            notificationClient.sendOrderConfirmation(
                savedOrder.getCustomerEmail(),
                "Замовлення #" + savedOrder.getId() + " створено",
                "Ваше замовлення успішно прийняте і обробляється."
            );
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
        
        return savedOrder;
    }

    public void processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check warehouse availability
        for (var item : order.getItems()) {
            boolean available = warehouseClient.checkAvailability(item.getProductId(), item.getQuantity());
            if (!available) {
                throw new RuntimeException("Product " + item.getProductName() + " not available");
            }
        }

        // Reserve items in warehouse
        for (var item : order.getItems()) {
            warehouseClient.reserveStock(item.getProductId(), item.getQuantity());
        }

        // Update order status
        orderRepository.updateStatus(orderId, OrderStatus.WAREHOUSE_PROCESSING);

        // Create delivery
        try {
            deliveryClient.createDelivery(orderId, order.getDeliveryAddress());
            orderRepository.updateStatus(orderId, OrderStatus.READY_FOR_DELIVERY);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create delivery: " + e.getMessage());
        }
    }
}
