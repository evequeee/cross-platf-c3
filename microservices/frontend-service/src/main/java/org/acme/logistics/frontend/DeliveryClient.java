package org.acme.logistics.frontend;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/api/delivery")
@RegisterRestClient(configKey = "delivery-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DeliveryClient {

    @GET
    List<Map<String, Object>> getAllDeliveries();

    @GET
    @Path("/{id}")
    Map<String, Object> getDelivery(@PathParam("id") Long id);

    @GET
    @Path("/track/{trackingNumber}")
    Map<String, Object> trackDelivery(@PathParam("trackingNumber") String trackingNumber);
}
