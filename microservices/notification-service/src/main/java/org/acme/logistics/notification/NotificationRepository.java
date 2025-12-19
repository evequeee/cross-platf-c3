package org.acme.logistics.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.*;

@ApplicationScoped
public class NotificationRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Notification> findAllNotifications() {
        return entityManager.createQuery("SELECT n FROM Notification n", Notification.class)
            .getResultList();
    }

    public Optional<Notification> findNotificationById(Long id) {
        Notification notification = entityManager.find(Notification.class, id);
        return Optional.ofNullable(notification);
    }

    public List<Notification> findByStatus(NotificationStatus status) {
        TypedQuery<Notification> query = entityManager.createQuery(
            "SELECT n FROM Notification n WHERE n.status = :status", Notification.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Notification> findByType(NotificationType type) {
        TypedQuery<Notification> query = entityManager.createQuery(
            "SELECT n FROM Notification n WHERE n.type = :type", Notification.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Notification> findByRecipient(String recipient) {
        TypedQuery<Notification> query = entityManager.createQuery(
            "SELECT n FROM Notification n WHERE n.recipient = :recipient", Notification.class);
        query.setParameter("recipient", recipient);
        return query.getResultList();
    }

    public List<Notification> findPendingNotifications() {
        TypedQuery<Notification> query = entityManager.createQuery(
            "SELECT n FROM Notification n WHERE n.status = :status AND n.retryCount < :maxRetries", 
            Notification.class);
        query.setParameter("status", NotificationStatus.PENDING);
        query.setParameter("maxRetries", 3);
        return query.getResultList();
    }

    @Transactional
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            entityManager.persist(notification);
            return notification;
        } else {
            return entityManager.merge(notification);
        }
    }
    
    @Transactional
    public Notification saveNotification(Notification notification) {
        return save(notification);
    }

    public Optional<Notification> findById(Long id) {
        return findNotificationById(id);
    }

    public List<Notification> findAll() {
        return findAllNotifications();
    }

    public List<Notification> findFailedWithRetries() {
        TypedQuery<Notification> query = entityManager.createQuery(
            "SELECT n FROM Notification n WHERE n.status = :status AND n.retryCount > 0", 
            Notification.class);
        query.setParameter("status", NotificationStatus.FAILED);
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        deleteNotificationById(id);
    }

    @Transactional
    public void deleteNotificationById(Long id) {
        Notification notification = entityManager.find(Notification.class, id);
        if (notification != null) {
            entityManager.remove(notification);
        }
    }
}
