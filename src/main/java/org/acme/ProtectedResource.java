package org.acme;

import jakarta.inject.Inject;
import org.keycloak.authorization.client.AuthzClient;

public class ProtectedResource {
    @Inject
    AuthzClient authzClient;
}
