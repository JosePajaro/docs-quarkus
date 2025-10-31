package org.acme.security.keycloak.authorization.interfaces;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import jakarta.enterprise.context.ApplicationScoped;

@ConfigMapping(prefix = "keycloak.admin")
@ApplicationScoped
public interface KeycloakAdminConfig {
    @WithName("server-url")
    String serverUrl();

    @WithName("realm")
    String realm();

    @WithName("realm-master")
    String realmMaster();
}
