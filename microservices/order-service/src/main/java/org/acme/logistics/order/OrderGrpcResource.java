package org.acme.logistics.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.logistics.order.grpc.WarehouseGrpcClient;
import org.acme.logistics.order.grpc.DeliveryGrpcClient;

@Path("/api/orders/grpc")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderGrpcResource {

    @Inject
    WarehouseGrpcClient warehouseGrpcClient;

    @Inject
    DeliveryGrpcClient deliveryGrpcClient;

    @GET
    @Path("/check-stock/{productId}")
    public Response checkStock(@PathParam("productId") Long productId, 
                               @QueryParam("quantity") Integer quantity) {
        boolean available = warehouseGrpcClient.checkStockAvailability(productId, quantity);
        return Response.ok(new StockCheckResponse(productId, quantity, available)).build();
    }

    @POST
    @Path("/reserve-stock/{productId}")
    public Response reserveStock(@PathParam("productId") Long productId, 
                                 @QueryParam("quantity") Integer quantity) {
        boolean success = warehouseGrpcClient.reserveStock(productId, quantity);
        return Response.ok(new ReserveResponse(success, 
            success ? "Товар зарезервовано" : "Не вдалося зарезервувати")).build();
    }

    @POST
    @Path("/create-delivery")
    public Response createDelivery(@QueryParam("orderId") Long orderId, 
                                   @QueryParam("address") String address) {
        var deliveryInfo = deliveryGrpcClient.createDelivery(orderId, address);
        return Response.ok(deliveryInfo).build();
    }

    @GET
    @Path("/track-delivery/{trackingNumber}")
    public Response trackDelivery(@PathParam("trackingNumber") String trackingNumber) {
        var trackInfo = deliveryGrpcClient.trackDelivery(trackingNumber);
        return Response.ok(trackInfo).build();
    }

    public static class StockCheckResponse {
        public Long productId;
        public Integer quantity;
        public boolean available;

        public StockCheckResponse(Long productId, Integer quantity, boolean available) {
            this.productId = productId;
            this.quantity = quantity;
            this.available = available;
        }
    }

    public static class ReserveResponse {
        public boolean success;
        public String message;

        public ReserveResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
