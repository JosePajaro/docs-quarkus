package org.acme.resources;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.security.keycloak.authorization.utils.TokenGenerator;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
public class CrudResourceTest {
    @Test
    void searchUsersClient(){
        String token = TokenGenerator.getUserToken("margarita.ortega", "password");
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/keycloak/user/search")
                .then()
                .statusCode(403);
    }

    @Test
    void searchUsersAdmin(){
        String token = TokenGenerator.getUserToken("jose.pajaro", "password");
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/keycloak/user/search")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }
}
