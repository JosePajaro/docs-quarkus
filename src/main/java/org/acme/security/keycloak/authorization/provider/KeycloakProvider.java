package org.acme.security.keycloak.authorization.provider;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.security.keycloak.authorization.interfaces.KeycloakAdminConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;

@Slf4j
@Startup
@ApplicationScoped
public class KeycloakProvider {

    @Inject
    KeycloakAdminConfig config;

    @PostConstruct
    void init() {
        if (config == null) {
            throw new IllegalStateException("KeycloakAdminConfig no fue inyectado correctamente");
        }
        log.info("✅ Keycloak config cargado: {}", config.serverUrl());
    }

    public RealmResource getRealmResource(String token){
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required to access Keycloak");
        }
        Keycloak keycloak = KeycloakBuilder
                .builder()
                .serverUrl(config.serverUrl())
                .realm(config.realmMaster())
                .authorization(token)
                .build();
        return keycloak.realm(config.realm());
    }

}
