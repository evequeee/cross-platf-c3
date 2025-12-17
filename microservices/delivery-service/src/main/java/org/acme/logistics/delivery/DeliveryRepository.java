package org.acme.logistics.delivery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.*;

@ApplicationScoped
public class DeliveryRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Delivery> findAllDeliveries() {
        return entityManager.createQuery("SELECT d FROM Delivery d", Delivery.class)
            .getResultList();
    }

    public Optional<Delivery> findDeliveryById(Long id) {
        Delivery delivery = entityManager.find(Delivery.class, id);
        return Optional.ofNullable(delivery);
    }

    public Optional<Delivery> findByOrderId(Long orderId) {
        TypedQuery<Delivery> query = entityManager.createQuery(
            "SELECT d FROM Delivery d WHERE d.orderId = :orderId", Delivery.class);
        query.setParameter("orderId", orderId);
        return query.getResultList().stream().findFirst();
    }

    public Optional<Delivery> findByTrackingNumber(String trackingNumber) {
        TypedQuery<Delivery> query = entityManager.createQuery(
            "SELECT d FROM Delivery d WHERE d.trackingNumber = :trackingNumber", Delivery.class);
        query.setParameter("trackingNumber", trackingNumber);
        return query.getResultList().stream().findFirst();
    }

    public List<Delivery> findByStatus(DeliveryStatus status) {
        TypedQuery<Delivery> query = entityManager.createQuery(
            "SELECT d FROM Delivery d WHERE d.status = :status", Delivery.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Delivery> findByDriverName(String driverName) {
        TypedQuery<Delivery> query = entityManager.createQuery(
            "SELECT d FROM Delivery d WHERE LOWER(d.driverName) LIKE LOWER(:driverName)", Delivery.class);
        query.setParameter("driverName", "%" + driverName + "%");
        return query.getResultList();
    }

    @Transactional
    public Delivery saveDelivery(Delivery delivery) {
        if (delivery.getId() == null) {
            entityManager.persist(delivery);
            return delivery;
        } else {
            return entityManager.merge(delivery);
        }
    }

    @Transactional
    public Delivery updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = entityManager.find(Delivery.class, id);
        if (delivery != null) {
            delivery.setStatus(status);
            entityManager.merge(delivery);
        }
        return delivery;
    }

    @Transactional
    public void deleteDeliveryById(Long id) {
        Delivery delivery = entityManager.find(Delivery.class, id);
        if (delivery != null) {
            entityManager.remove(delivery);
        }
    }
}
