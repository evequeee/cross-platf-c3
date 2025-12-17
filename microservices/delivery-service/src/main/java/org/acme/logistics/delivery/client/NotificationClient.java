package org.acme.logistics.delivery.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/notifications")
@RegisterRestClient(configKey = "notification-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NotificationClient {

    @POST
    @Path("/send")
    void sendNotification(
        @QueryParam("recipient") String recipient,
        @QueryParam("type") String type,
        @QueryParam("subject") String subject,
        @QueryParam("message") String message
    );
}
