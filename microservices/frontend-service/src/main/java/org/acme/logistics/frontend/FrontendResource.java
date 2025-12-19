package org.acme.logistics.frontend;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class FrontendResource {

    @Inject
    Template index;

    @Inject
    Template dashboard;

    @Inject
    Template orders;

    @Inject
    Template warehouse;

    @Inject
    Template deliveries;

    @Inject
    Template login;

    @Inject
    SecurityIdentity identity;

    @Inject
    @RestClient
    OrderClient orderClient;

    @Inject
    @RestClient
    WarehouseClient warehouseClient;

    @Inject
    @RestClient
    DeliveryClient deliveryClient;

    @GET
    public TemplateInstance home() {
        String username = identity.isAnonymous() ? null : identity.getPrincipal().getName();
        return index.data("username", username);
    }

    @GET
    @Path("/login")
    public TemplateInstance loginPage() {
        return login.instance();
    }

    @GET
    @Path("/dashboard")
    public TemplateInstance dashboardPage() {
        String username = identity.isAnonymous() ? "Guest" : identity.getPrincipal().getName();
        
        // Get statistics
        int orderCount = 0;
        int warehouseCount = 0;
        int deliveryCount = 0;
        
        try {
            orderCount = orderClient.getAllOrders().size();
        } catch (Exception e) { /* ignore */ }
        
        try {
            warehouseCount = warehouseClient.getAllItems().size();
        } catch (Exception e) { /* ignore */ }
        
        try {
            deliveryCount = deliveryClient.getAllDeliveries().size();
        } catch (Exception e) { /* ignore */ }
        
        return dashboard
                .data("username", username)
                .data("orderCount", orderCount)
                .data("warehouseCount", warehouseCount)
                .data("deliveryCount", deliveryCount);
    }

    @GET
    @Path("/orders")
    public TemplateInstance ordersPage() {
        List<Map<String, Object>> orderList = List.of();
        try {
            orderList = orderClient.getAllOrders();
        } catch (Exception e) { /* ignore */ }
        
        return orders.data("orders", orderList);
    }

    @GET
    @Path("/warehouse")
    public TemplateInstance warehousePage() {
        List<Map<String, Object>> items = List.of();
        try {
            items = warehouseClient.getAllItems();
        } catch (Exception e) { /* ignore */ }
        
        return warehouse.data("items", items);
    }

    @GET
    @Path("/deliveries")
    public TemplateInstance deliveriesPage() {
        List<Map<String, Object>> deliveryList = List.of();
        try {
            deliveryList = deliveryClient.getAllDeliveries();
        } catch (Exception e) { /* ignore */ }
        
        return deliveries.data("deliveries", deliveryList);
    }

    @GET
    @Path("/logout")
    public Response logout() {
        return Response.seeOther(URI.create("/")).build();
    }
}
