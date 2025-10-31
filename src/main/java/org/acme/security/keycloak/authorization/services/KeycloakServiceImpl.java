package org.acme.security.keycloak.authorization.services;

import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.acme.security.keycloak.authorization.controller.dto.UserDTO;
import org.acme.security.keycloak.authorization.interfaces.IKeycloakServices;
import org.acme.security.keycloak.authorization.provider.KeycloakProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class KeycloakServiceImpl implements IKeycloakServices {

    @Inject
    KeycloakProvider keycloakProvider;

    public RealmResource getRealm(String token){
        return keycloakProvider.getRealmResource(token);
    }

    /**
     * Metodo para listar todos los usuarios de keycloak
     * @param token
     * @return List<UserRepresentation>
    */
    @Override
    public List<UserRepresentation> findAllUsers(String token) {

        return getRealm(token)
                .users()
                .list();
    }

    /**
     * Metodo para buscar un usuario por su username
     * @param token
     * @param username
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> searchUserByUsername(String token, String username) {
        return getRealm(token)
                .users()
                .search(username, true);
    }

    /**
     * Metodo para crear un usuario en keycloak
     * @param token
     * @param userDTO
     * @return String
     */
    @Override
    public String createUser(String token, @NonNull UserDTO userDTO) {
        int status = 0;
        UsersResource usersResource = getRealm(token).users();
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.username());
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(userDTO.email());
        user.setEmailVerified(true); //por efectos practicos colocaremos la verificación de email en true pero es recomendable para un ambiente de producción que se envíe una verificación al correo
        user.setEnabled(true);
        Response response = usersResource.create(user);
        status = response.getStatus();

        if (status == 201){
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDTO.password());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = getRealm(token);

            List<RoleRepresentation> roleRepresentations = null;

            if (userDTO.roles() == null || userDTO.roles().isEmpty()){
                roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());
            } else {
                roleRepresentations = realmResource
                        .roles()
                        .list()
                        .stream()
                        .filter(role -> userDTO.roles()
                                .stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                        .toList();
            }
            realmResource.users().get(userId).roles().realmLevel().add(roleRepresentations);

            return "User Create Successfully!!!";
        } else if (status == 409){
            log.error("user already exists");
            return "user already exists";
        } else{
            log.error("Error creating user, please contact with the administrator!!");
            return "Error creating user, please contact with the administrator!!";
        }
    }

    /**
     * Metodo para eliminar un usuario de keycloak
     * @param token
     * @param userId
     * @return String
     */
    @Override
    public void deleteUser(String token,String userId) {
        getRealm(token).users().get(userId).remove();
    }

    /**
     * Metodo para actualizar un usuario de keycloak
     * @param token
     * @param userId
     * @param userDTO
     * @return String
     */
    @Override
    public void updateUser(String token, String userId, UserDTO userDTO) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userDTO.password());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userDTO.username());
        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UsersResource users = getRealm(token).users();
        users.get(userId).update(userRepresentation);
        users.get(userId).resetPassword(credentialRepresentation);

    }
}
