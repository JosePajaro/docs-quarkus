package org.acme.security.keycloak.authorization.domain.modal;

import io.quarkus.security.identity.SecurityIdentity;

public class User{
    private final String userName;

    public User(@org.jetbrains.annotations.NotNull SecurityIdentity identity) {
        this.userName = identity.getPrincipal().getName();
    }

    public String getUserName() {
        return userName;
    }
}
