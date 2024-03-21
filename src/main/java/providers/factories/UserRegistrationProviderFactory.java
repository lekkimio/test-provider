package providers.factories;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import providers.UserRegistrationProvider;

public class UserRegistrationProviderFactory implements EventListenerProviderFactory {

    private static final String PROVIDER_ID = "custom-user-registration-provider";

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new UserRegistrationProvider(keycloakSession);

    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
