package org.acme.logistics.delivery;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/delivery")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryResource {

    @Inject
    DeliveryRepository deliveryRepository;

    @Inject
    DeliveryService deliveryService;

    @GET
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAllDeliveries();
    }

    @GET
    @Path("/{id}")
    public Response getDeliveryById(@PathParam("id") Long id) {
        return deliveryRepository.findDeliveryById(id)
            .map(delivery -> Response.ok(delivery).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/order/{orderId}")
    public Response getDeliveryByOrderId(@PathParam("orderId") Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
            .map(delivery -> Response.ok(delivery).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/track/{trackingNumber}")
    public Response trackDelivery(@PathParam("trackingNumber") String trackingNumber) {
        return deliveryRepository.findByTrackingNumber(trackingNumber)
            .map(delivery -> Response.ok(delivery).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/status/{status}")
    public List<Delivery> getDeliveriesByStatus(@PathParam("status") DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    @POST
    public Response createDelivery(@QueryParam("orderId") Long orderId, 
                                   @QueryParam("address") String address) {
        try {
            Delivery delivery = deliveryService.createDelivery(orderId, address);
            return Response.status(Response.Status.CREATED).entity(delivery).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, 
                                 @QueryParam("status") DeliveryStatus status) {
        try {
            deliveryService.updateDeliveryStatus(id, status);
            return Response.ok(new SuccessResponse("Статус доставки оновлено")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/location")
    public Response updateLocation(@PathParam("id") Long id, 
                                   @QueryParam("location") String location) {
        return deliveryRepository.findDeliveryById(id)
            .map(delivery -> {
                delivery.setCurrentLocation(location);
                deliveryRepository.saveDelivery(delivery);
                return Response.ok(delivery).build();
            })
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDelivery(@PathParam("id") Long id) {
        deliveryRepository.deleteById(id);
        return Response.noContent().build();
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
