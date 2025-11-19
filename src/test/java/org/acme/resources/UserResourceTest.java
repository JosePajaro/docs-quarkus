package org.acme.resources;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.security.keycloak.authorization.utils.TokenGenerator;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
public class UserResourceTest {
    @Test
    void UsersMe() {
        String token = TokenGenerator.getUserToken("jose.pajaro", "password");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/users/me")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("userName", is("jose.pajaro"));
    }

    @Test
    void Admin200() {
        String token = TokenGenerator.getUserToken("jose.pajaro", "password");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/admin")
                .then()
                .statusCode(200)
                .body(is("granted"));
    }

    @Test
    void Admin403() {
        String token = TokenGenerator.getUserToken("margarita.ortega", "password");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/admin")
                .then()
                .statusCode(403);
    }
}
