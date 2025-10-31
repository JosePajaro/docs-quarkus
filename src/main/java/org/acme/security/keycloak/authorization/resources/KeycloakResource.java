package org.acme.security.keycloak.authorization.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.acme.security.keycloak.authorization.controller.dto.UserDTO;
import org.acme.security.keycloak.authorization.interfaces.IKeycloakServices;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Path("/keycloak/user")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KeycloakResource {

    @Inject
    IKeycloakServices keycloakServices;

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new WebApplicationException("Missing or invalid Authorization header", Response.Status.UNAUTHORIZED);
        }
        return authHeader.substring("Bearer ".length()).trim();
    }

    @GET
    @Path("/search")
    public Response findAllUsers(@HeaderParam("Authorization") String authorizationHeader) {
        try {
            String token = extractToken(authorizationHeader);
            return Response.ok(keycloakServices.findAllUsers(token)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/search/{userName}")
    public Response searchUserByUsername(@HeaderParam("Authorization") String authorizationHeader,
                                         @PathParam("userName") String userName) {
        String token = extractToken(authorizationHeader);
        return Response.ok(keycloakServices.searchUserByUsername(token, userName)).build();
    }

    @POST
    @Path("/create")
    public Response createUser(@HeaderParam("Authorization") String authorizationHeader,
                               UserDTO userDTO,
                               @Context UriInfo uriInfo) throws URISyntaxException {
        String token = extractToken(authorizationHeader);
        String userId = keycloakServices.createUser(token, userDTO);
        return Response.created(new URI("/api/keycloak/user/create"))
                .entity(Map.of("message", "User created successfully", "id", userId))
                .build();
    }

    @PUT
    @Path("/update/{userId}")
    public Response updateUser(@HeaderParam("Authorization") String authorizationHeader,
                               @PathParam("userId") String userId,
                               UserDTO userDTO) {
        String token = extractToken(authorizationHeader);
        keycloakServices.updateUser(token, userId, userDTO);
        return Response.ok(Map.of("message", "User updated successfully")).build();
    }

    @DELETE
    @Path("/delete/{userId}")
    public Response deleteUser(@HeaderParam("Authorization") String authorizationHeader,
                               @PathParam("userId") String userId) {
        String token = extractToken(authorizationHeader);
        keycloakServices.deleteUser(token, userId);
        return Response.ok(Map.of("message", "User deleted successfully")).build();
    }
}
