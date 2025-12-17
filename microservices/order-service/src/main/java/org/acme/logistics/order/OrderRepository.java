package org.acme.logistics.order;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public List<Order> findAllOrders() {
        return listAll();
    }

    public Optional<Order> findOrderById(Long id) {
        return findByIdOptional(id);
    }

    public Order saveOrder(Order order) {
        persist(order);
        return order;
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        if (order != null) {
            order.setStatus(status);
            persist(order);
        }
        return order;
    }

    public void deleteOrderById(Long id) {
        deleteById(id);
    }

    public List<Order> findByStatus(OrderStatus status) {
        return list("status", status);
    }

    public List<Order> findByCustomerEmail(String email) {
        return list("customerEmail", email);
    }
}
