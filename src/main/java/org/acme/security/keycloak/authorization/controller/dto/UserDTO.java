package org.acme.security.keycloak.authorization.controller.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserDTO(String username, String firstName, String lastName,String email, String password, Set<String> roles) {
}
