package org.acme.warehouse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/warehouse")
@RegisterRestClient(configKey = "warehouse-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WarehouseClient {

    @GET
    @Path("/check-availability")
    boolean checkAvailability(@QueryParam("productId") Long productId, 
                             @QueryParam("quantity") Integer quantity);

    @POST
    @Path("/reserve")
    void reserveStock(@QueryParam("productId") Long productId, 
                     @QueryParam("quantity") Integer quantity);
}
