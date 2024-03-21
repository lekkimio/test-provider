package providers;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;


public class UserRegistrationProvider implements EventListenerProvider{

    private KeycloakSession keycloakSession;

    public UserRegistrationProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public void onEvent(Event event) {

        System.out.println("User with email " + event.getUserId() + " has been registered"); ;


    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        System.out.println("User with ya ebanvl " + adminEvent.getAuthDetails().getUserId() + " has been created");


    }

    @Override
    public void close() {

    }
}
