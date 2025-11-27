package org.acme.delivery;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/deliveries")
@RegisterRestClient(configKey = "delivery-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DeliveryClient {

    @POST
    Response createDelivery(@QueryParam("orderId") Long orderId, 
                           @QueryParam("address") String address);

    @PUT
    @Path("/{id}/status")
    Response updateStatus(@PathParam("id") Long id, 
                         @QueryParam("status") DeliveryStatus status);
}
