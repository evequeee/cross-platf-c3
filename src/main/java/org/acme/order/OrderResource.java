package org.acme.order;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class OrderResource {

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderService orderService;

    @GET
    @RolesAllowed({"user", "admin"})
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response getOrder(@PathParam("id") Long id) {
        return orderRepository.findById(id)
            .map(order -> Response.ok(order).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({"user", "admin"})
    public Response createOrder(Order order) {
        try {
            Order created = orderService.createOrder(order);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    @RolesAllowed("admin")
    public Response updateOrderStatus(@PathParam("id") Long id, @QueryParam("status") OrderStatus status) {
        Order updated = orderRepository.updateStatus(id, status);
        if (updated != null) {
            return Response.ok(updated).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/process")
    @RolesAllowed("admin")
    public Response processOrder(@PathParam("id") Long id) {
        try {
            orderService.processOrder(id);
            return Response.ok().build();
        } catch (Exception e) {
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
