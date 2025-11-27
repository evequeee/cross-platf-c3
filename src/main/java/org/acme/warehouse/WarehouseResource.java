package org.acme.warehouse;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class WarehouseResource {

    @Inject
    WarehouseRepository warehouseRepository;

    @GET
    @Path("/items")
    @RolesAllowed({"user", "admin"})
    public List<WarehouseItem> getAllItems() {
        return warehouseRepository.findAll();
    }

    @GET
    @Path("/items/{id}")
    @RolesAllowed({"user", "admin"})
    public Response getItem(@PathParam("id") Long id) {
        return warehouseRepository.findById(id)
            .map(item -> Response.ok(item).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/products/{productId}")
    @RolesAllowed({"user", "admin"})
    public Response getItemByProductId(@PathParam("productId") Long productId) {
        return warehouseRepository.findByProductId(productId)
            .map(item -> Response.ok(item).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/check-availability")
    @RolesAllowed({"user", "admin"})
    public boolean checkAvailability(@QueryParam("productId") Long productId, 
                                     @QueryParam("quantity") Integer quantity) {
        return warehouseRepository.checkAvailability(productId, quantity);
    }

    @POST
    @Path("/reserve")
    @RolesAllowed("admin")
    public Response reserveStock(@QueryParam("productId") Long productId, 
                                 @QueryParam("quantity") Integer quantity) {
        try {
            warehouseRepository.reserveStock(productId, quantity);
            return Response.ok().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
