package org.acme.security.keycloak.authorization.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class TokenGenerator {

    private static final String TOKEN_URL = "http://localhost:9090/realms/quarkus-crud-backend-service-dev/protocol/openid-connect/token";
    private static final String CLIENT_ID = "quarkus-client-api-rest";
    private static final String CLIENT_SECRET = "yTHjMMASZxjDSfhWr1ygcc1ZVn4LfxpB";

    /**
     * Genera un token de acceso usando el flujo "password grant type"
     * con usuario y contraseña de Keycloak.
     *
     * @param username Usuario de Keycloak
     * @param password Contraseña del usuario
     * @return access_token válido para usar en peticiones
     */
    public static String getUserToken(String username, String password) {
        try {
            String form = buildForm(username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("❌ Error obteniendo token: " + response.statusCode() + " → " + response.body());
            }

            JSONObject json = new JSONObject(response.body());
            return json.getString("access_token");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildForm(String username, String password) {
        return "client_id=" + encode(CLIENT_ID)
                + "&client_secret=" + encode(CLIENT_SECRET)
                + "&grant_type=password"
                + "&username=" + encode(username)
                + "&password=" + encode(password);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
