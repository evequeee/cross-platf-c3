package org.acme.logistics.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderService orderService;

    @GET
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrders();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        return orderRepository.findOrderById(id)
            .map(order -> Response.ok(order).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/status/{status}")
    public List<Order> getOrdersByStatus(@PathParam("status") OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @POST
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
    public Response updateOrderStatus(@PathParam("id") Long id, @QueryParam("status") OrderStatus status) {
        Order updated = orderRepository.updateStatus(id, status);
        if (updated != null) {
            return Response.ok(updated).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/process")
    public Response processOrder(@PathParam("id") Long id) {
        try {
            orderService.processOrder(id);
            return Response.ok().entity(new SuccessResponse("Замовлення успішно обробляється")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        orderRepository.deleteById(id);
        return Response.noContent().build();
    }

    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    public static class SuccessResponse {
        public String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
