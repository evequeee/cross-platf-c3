package org.acme.logistics.frontend;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/api/warehouse")
@RegisterRestClient(configKey = "warehouse-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WarehouseClient {

    @GET
    List<Map<String, Object>> getAllItems();

    @GET
    @Path("/{id}")
    Map<String, Object> getItem(@PathParam("id") Long id);
}
