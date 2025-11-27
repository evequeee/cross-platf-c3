package org.acme.notification;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/notifications")
@RegisterRestClient(configKey = "notification-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NotificationClient {

    @POST
    @Path("/order-confirmation")
    Response sendOrderConfirmation(@QueryParam("email") String email,
                                  @QueryParam("subject") String subject,
                                  @QueryParam("message") String message);
}
