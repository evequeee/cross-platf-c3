package org.acme.logistics.order.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/delivery")
@RegisterRestClient(configKey = "delivery-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DeliveryClient {

    @POST
    void createDelivery(@QueryParam("orderId") Long orderId, @QueryParam("address") String address);

    @GET
    @Path("/order/{orderId}")
    DeliveryInfo getDeliveryByOrderId(@PathParam("orderId") Long orderId);

    class DeliveryInfo {
        public Long id;
        public Long orderId;
        public String status;
        public String driverName;
        public String estimatedDeliveryTime;
    }
}
