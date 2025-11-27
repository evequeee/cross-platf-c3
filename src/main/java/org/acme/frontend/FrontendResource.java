package org.acme.frontend;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.acme.order.Order;
import org.acme.order.OrderService;
import org.acme.warehouse.WarehouseClient;
import org.acme.delivery.DeliveryClient;
import io.quarkus.security.identity.SecurityIdentity;

import java.util.List;

@Path("/")
public class FrontendResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    OrderService orderService;

    @Inject
    @RestClient
    WarehouseClient warehouseClient;

    @Inject
    @RestClient
    DeliveryClient deliveryClient;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index();
        public static native TemplateInstance dashboard(SecurityIdentity identity, List<Order> orders);
        public static native TemplateInstance login();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return Templates.index();
    }

    @GET
    @Path("/dashboard")
    @Authenticated
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance dashboard() {
        List<Order> orders = orderService.getAllOrders();
        return Templates.dashboard(securityIdentity, orders);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance login() {
        return Templates.login();
    }
}
