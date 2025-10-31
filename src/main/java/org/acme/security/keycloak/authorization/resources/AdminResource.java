package org.acme.security.keycloak.authorization.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.security.Authenticated;

@Path("/api/admin")
@Authenticated
public class AdminResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("admin")
    public String admin() {
        return "granted";
    }
}