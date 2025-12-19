package org.acme.logistics.notification;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllNotifications();
    }

    @GET
    @Path("/{id}")
    public Response getNotificationById(@PathParam("id") Long id) {
        return notificationRepository.findNotificationById(id)
            .map(notification -> Response.ok(notification).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/recipient/{recipient}")
    public List<Notification> getNotificationsByRecipient(@PathParam("recipient") String recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    @GET
    @Path("/status/{status}")
    public List<Notification> getNotificationsByStatus(@PathParam("status") NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    @GET
    @Path("/type/{type}")
    public List<Notification> getNotificationsByType(@PathParam("type") NotificationType type) {
        return notificationRepository.findByType(type);
    }

    @GET
    @Path("/pending")
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findPendingNotifications();
    }

    @POST
    @Path("/send")
    public Response sendNotification(
            @QueryParam("recipient") String recipient,
            @QueryParam("type") String type,
            @QueryParam("subject") String subject,
            @QueryParam("message") String message) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            Notification notification = notificationService.sendNotification(
                recipient, notificationType, subject, message
            );
            return Response.status(Response.Status.CREATED).entity(notification).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    public Response createNotification(Notification notification) {
        Notification saved = notificationRepository.saveNotification(notification);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, 
                                 @QueryParam("status") NotificationStatus status) {
        return notificationRepository.findNotificationById(id)
            .map(notification -> {
                notification.setStatus(status);
                Notification updated = notificationRepository.save(notification);
                return Response.ok(updated).build();
            })
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/{id}/retry")
    public Response retryNotification(@PathParam("id") Long id) {
        try {
            notificationService.retryNotification(id);
            return Response.ok(new SuccessResponse("Повідомлення повторно відправлено")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteNotification(@PathParam("id") Long id) {
        notificationRepository.deleteById(id);
        return Response.noContent().build();
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }

    public static class SuccessResponse {
        public String message;
        public SuccessResponse(String message) { this.message = message; }
    }
}
