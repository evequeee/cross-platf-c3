package org.acme.logistics.order.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/warehouse")
@RegisterRestClient(configKey = "warehouse-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WarehouseClient {

    @GET
    @Path("/check/{productId}")
    boolean checkAvailability(@PathParam("productId") Long productId, @QueryParam("quantity") Integer quantity);

    @POST
    @Path("/reserve/{productId}")
    void reserveStock(@PathParam("productId") Long productId, @QueryParam("quantity") Integer quantity);
}
