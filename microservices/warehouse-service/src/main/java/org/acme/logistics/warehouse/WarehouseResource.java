package org.acme.logistics.warehouse;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WarehouseResource {

    @Inject
    WarehouseRepository warehouseRepository;

    @GET
    public List<WarehouseItem> getAllItems() {
        return warehouseRepository.findAllItems();
    }

    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Long id) {
        return warehouseRepository.findItemById(id)
            .map(item -> Response.ok(item).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/product/{productId}")
    public Response getItemByProductId(@PathParam("productId") Long productId) {
        return warehouseRepository.findByProductId(productId)
            .map(item -> Response.ok(item).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/category/{category}")
    public List<WarehouseItem> getItemsByCategory(@PathParam("category") String category) {
        return warehouseRepository.findByCategory(category);
    }

    @GET
    @Path("/check/{productId}")
    public boolean checkAvailability(@PathParam("productId") Long productId, 
                                    @QueryParam("quantity") Integer quantity) {
        return warehouseRepository.checkAvailability(productId, quantity);
    }

    @POST
    @Path("/reserve/{productId}")
    public Response reserveStock(@PathParam("productId") Long productId, 
                                 @QueryParam("quantity") Integer quantity) {
        try {
            warehouseRepository.reserveStock(productId, quantity);
            return Response.ok(new SuccessResponse("Товар успішно зарезервовано")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/release/{productId}")
    public Response releaseStock(@PathParam("productId") Long productId, 
                                 @QueryParam("quantity") Integer quantity) {
        try {
            warehouseRepository.releaseStock(productId, quantity);
            return Response.ok(new SuccessResponse("Резервація знята")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/remove/{productId}")
    public Response removeStock(@PathParam("productId") Long productId, 
                                @QueryParam("quantity") Integer quantity) {
        try {
            warehouseRepository.removeStock(productId, quantity);
            return Response.ok(new SuccessResponse("Товар видалено зі складу")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/add/{productId}")
    public Response addStock(@PathParam("productId") Long productId, 
                            @QueryParam("quantity") Integer quantity) {
        try {
            warehouseRepository.addStock(productId, quantity);
            return Response.ok(new SuccessResponse("Товар додано на склад")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }

    public static class SuccessResponse {
        public String message;
        public SuccessResponse(String message) { this.message = message; }
    }
}
