package org.acme.security.keycloak.authorization.services;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final String realm = "quarkus"; // tu realm

    public KeycloakAdminService() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8543")
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("quarkus-admin-client") // tu nuevo cliente
                .clientSecret("TU_CLIENT_SECRET") // cámbialo por el real
                .build();
    }

    private RealmResource getRealm() {
        return keycloak.realm(realm);
    }
    public String CreateUser(String username, String email, String password) {
        UsersResource usersResource = getRealm().users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Error creando usuario: " + response.getStatusInfo());
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        setPassword(userId, password);
        return userId;
    }

    public void setPassword(String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        getRealm().users().get(userId).resetPassword(credential);
    }

    public void deleteUser(String userId) {
        getRealm().users().get(userId).remove();
    }

    public List<UserRepresentation> listUsers() {
        return getRealm().users().list();
    }
}
