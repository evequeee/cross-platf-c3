package org.acme.logistics.order;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Захищені ендпоінти замовлень (для production режиму з Keycloak)
 */
@Path("/api/secure/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class SecureOrderResource {

    @GET
    @RolesAllowed({"user", "admin"})
    public Response getSecureOrders() {
        return Response.ok("{\"message\": \"Це захищений ендпоінт. Тільки авторизовані користувачі.\"}").build();
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    public Response getAdminOrders() {
        return Response.ok("{\"message\": \"Це адміністративний ендпоінт. Тільки адміни.\"}").build();
    }
}
