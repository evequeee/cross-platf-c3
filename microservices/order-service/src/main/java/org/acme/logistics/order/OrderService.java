package org.acme.logistics.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.acme.logistics.order.client.WarehouseClient;
import org.acme.logistics.order.client.DeliveryClient;
import org.acme.logistics.order.client.NotificationClient;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class OrderService {

    private static final Logger LOG = Logger.getLogger(OrderService.class.getName());

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
        LOG.info("Створення нового замовлення для клієнта: " + order.getCustomerName());
        
        // Валідація та збереження замовлення
        Order savedOrder = orderRepository.save(order);
        
        // Відправка повідомлення про створення замовлення
        try {
            notificationClient.sendNotification(
                savedOrder.getCustomerEmail(),
                "EMAIL",
                "Замовлення #" + savedOrder.getId() + " створено",
                "Ваше замовлення успішно прийняте і обробляється. Загальна сума: " + savedOrder.getTotalPrice() + " грн"
            );
            LOG.info("Повідомлення про створення замовлення відправлено");
        } catch (Exception e) {
            LOG.warning("Не вдалося відправити повідомлення: " + e.getMessage());
        }
        
        return savedOrder;
    }

    public void processOrder(Long orderId) {
        LOG.info("Початок обробки замовлення #" + orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Замовлення не знайдено"));

        // Крок 1: Перевірка наявності товарів на складі
        LOG.info("Перевірка наявності товарів на складі");
        for (var item : order.getItems()) {
            try {
                boolean available = warehouseClient.checkAvailability(item.getProductId(), item.getQuantity());
                if (!available) {
                    throw new RuntimeException("Товар '" + item.getProductName() + "' недоступний в необхідній кількості");
                }
            } catch (Exception e) {
                LOG.warning("Помилка перевірки складу: " + e.getMessage());
                throw new RuntimeException("Помилка зв'язку зі складом");
            }
        }

        // Крок 2: Резервування товарів
        LOG.info("Резервування товарів на складі");
        for (var item : order.getItems()) {
            try {
                warehouseClient.reserveStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                LOG.warning("Помилка резервування: " + e.getMessage());
                throw new RuntimeException("Не вдалося зарезервувати товари");
            }
        }

        orderRepository.updateStatus(orderId, OrderStatus.WAREHOUSE_RESERVED);

        // Крок 3: Створення доставки
        LOG.info("Створення доставки");
        try {
            deliveryClient.createDelivery(orderId, order.getDeliveryAddress());
            orderRepository.updateStatus(orderId, OrderStatus.READY_FOR_DELIVERY);
            
            // Повідомлення про готовність до доставки
            notificationClient.sendNotification(
                order.getCustomerEmail(),
                "EMAIL",
                "Замовлення #" + orderId + " готове до доставки",
                "Ваше замовлення підготовлено та очікує на відправку."
            );
            
            LOG.info("Замовлення #" + orderId + " успішно оброблено");
        } catch (Exception e) {
            LOG.warning("Помилка створення доставки: " + e.getMessage());
            throw new RuntimeException("Не вдалося створити доставку");
        }
    }
}
