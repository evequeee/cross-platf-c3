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
import org.acme.warehouse.WarehouseItem;
import org.acme.delivery.Delivery;
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
        public static native TemplateInstance dashboard(String username, List<Order> orders, 
                                                        List<WarehouseItem> items, 
                                                        List<Delivery> deliveries);
        public static native TemplateInstance login();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        if (securityIdentity.isAnonymous()) {
            return Templates.login();
        }
        return Templates.index();
    }

    @GET
    @Path("/dashboard")
    @Authenticated
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance dashboard() {
        String username = securityIdentity.getPrincipal().getName();
        
        List<Order> orders = orderService.getAllOrders();
        List<WarehouseItem> items = List.of(); // warehouseClient will be called via REST
        List<Delivery> deliveries = List.of();
        
        try {
            // These calls will work when services are protected properly
            // items = warehouseClient.getAllItems();
            // deliveries = deliveryClient.getAllDeliveries();
        } catch (Exception e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }
        
        return Templates.dashboard(username, orders, items, deliveries);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance login() {
        return Templates.login();
    }
}
