package org.acme.logistics.frontend;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/api/orders")
@RegisterRestClient(configKey = "order-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OrderClient {

    @GET
    List<Map<String, Object>> getAllOrders();

    @GET
    @Path("/{id}")
    Map<String, Object> getOrder(@PathParam("id") Long id);

    @POST
    Map<String, Object> createOrder(Map<String, Object> order);
}
