package org.acme.security.keycloak.authorization.interfaces;

import org.acme.security.keycloak.authorization.controller.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IKeycloakServices {
    List<UserRepresentation> findAllUsers(String token);

    List<UserRepresentation> searchUserByUsername(String token, String username);

    String createUser(String token, UserDTO userDTO);

    void updateUser(String token, String userId, UserDTO userDTO);

    void deleteUser(String token, String userId);
}
