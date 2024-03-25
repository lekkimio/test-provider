package providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.UserDto;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class UserRegistrationProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(UserRegistrationProvider.class.getName());

    private static final String API_URL = "http://host.docker.internal:8081/auth/keycloak-register";

    private KeycloakSession keycloakSession;

    public UserRegistrationProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public void onEvent(Event event) {
        log.info("Event: " + event.getType());
        if (event.getType().equals(EventType.REGISTER)) {

            UserModel userDetails = getUserDetailsFromKeycloak(event.getUserId());

            Long userId;
            try {
                userId = sendRequestToApi(userDetails);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            userDetails.setSingleAttribute("client_id", userId.toString());

        }

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        log.info("Admin Event: " + adminEvent.getOperationType());

        if (adminEvent.getOperationType().equals(OperationType.CREATE)) {
            String usernameFromAdminEvent = getUsernameFromAdminEvent(adminEvent.getRepresentation());

            UserModel userDetails = keycloakSession.users().getUserByUsername(keycloakSession.getContext().getRealm(), usernameFromAdminEvent);

            Long userId;
            try {
                userId = sendRequestToApi(userDetails);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            userDetails.setSingleAttribute("client_id", userId.toString());
        }

    }

    @Override
    public void close() {

    }

    private Long sendRequestToApi(UserModel userDetails) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        UserDto userDto = new UserDto();
        userDto.setEmail(userDetails.getEmail());
        userDto.setUsername(userDetails.getUsername());
        userDto.setFirstName(userDetails.getFirstName());
        userDto.setLastName(userDetails.getLastName());

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(userDto))).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        return Long.parseLong(responseBody);
    }

    private UserModel getUserDetailsFromKeycloak(String userKeycloakId) {
        return keycloakSession.users().getUserById(keycloakSession.getContext().getRealm(), userKeycloakId);
    }

    private String getUsernameFromAdminEvent(String representation) {
        return representation.split(",")[0].split(":")[1].replace("\"", "");
    }

}
