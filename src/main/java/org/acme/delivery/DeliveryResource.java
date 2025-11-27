package org.acme.delivery;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class DeliveryResource {

    @Inject
    DeliveryRepository deliveryRepository;

    @GET
    @RolesAllowed({"user", "admin"})
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response getDelivery(@PathParam("id") Long id) {
        return deliveryRepository.findById(id)
            .map(delivery -> Response.ok(delivery).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/order/{orderId}")
    @RolesAllowed({"user", "admin"})
    public Response getDeliveryByOrderId(@PathParam("orderId") Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
            .map(delivery -> Response.ok(delivery).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("admin")
    public Response createDelivery(@QueryParam("orderId") Long orderId, 
                                   @QueryParam("address") String address) {
        Delivery delivery = new Delivery(
            null,
            orderId,
            "Автоматично призначений водій",
            "+380501111111",
            "Центральний склад",
            address,
            LocalDateTime.now().plusHours(4)
        );
        
        Delivery saved = deliveryRepository.save(delivery);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @Path("/{id}/status")
    @RolesAllowed("admin")
    public Response updateDeliveryStatus(@PathParam("id") Long id, 
                                        @QueryParam("status") DeliveryStatus status) {
        Delivery updated = deliveryRepository.updateStatus(id, status);
        if (updated != null) {
            return Response.ok(updated).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
