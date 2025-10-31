package org.acme.security.keycloak.authorization.resources;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.acme.security.keycloak.authorization.domain.modal.User;
import org.jboss.resteasy.reactive.NoCache;

@Path("/api/users")
public class UserResource {
    @Inject
    SecurityIdentity identity;

    @GET
    @Path("me")
    @NoCache
    @RolesAllowed({"admin","user"})
    public User me() {
        return new User(identity);
    }

}
