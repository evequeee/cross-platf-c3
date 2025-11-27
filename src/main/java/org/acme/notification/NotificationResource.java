package org.acme.notification;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class NotificationResource {

    @Inject
    NotificationRepository notificationRepository;

    @GET
    @RolesAllowed("admin")
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response getNotification(@PathParam("id") Long id) {
        return notificationRepository.findById(id)
            .map(notification -> Response.ok(notification).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/recipient/{email}")
    @RolesAllowed({"user", "admin"})
    public List<Notification> getNotificationsByRecipient(@PathParam("email") String email) {
        return notificationRepository.findByRecipient(email);
    }

    @POST
    @Path("/send")
    @RolesAllowed("admin")
    public Response sendNotification(@QueryParam("recipient") String recipient,
                                    @QueryParam("subject") String subject,
                                    @QueryParam("message") String message) {
        Notification notification = new Notification(
            null,
            recipient,
            NotificationType.EMAIL,
            subject,
            message
        );
        
        Notification sent = notificationRepository.send(notification);
        return Response.status(Response.Status.CREATED).entity(sent).build();
    }

    @POST
    @Path("/order-confirmation")
    @RolesAllowed("admin")
    public Response sendOrderConfirmation(@QueryParam("email") String email,
                                         @QueryParam("subject") String subject,
                                         @QueryParam("message") String message) {
        return sendNotification(email, subject, message);
    }
}
