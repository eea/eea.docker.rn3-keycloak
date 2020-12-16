package org.eea.keycloak.event.mail;

import java.util.HashSet;
import java.util.Set;
import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class EmailEventListenerProviderFactory implements EventListenerProviderFactory {

  private static final Set<EventType> SUPPORTED_EVENTS = new HashSet<>();

  private Set<EventType> includedEvents = new HashSet<>();

  @Override
  public EventListenerProvider create(KeycloakSession session) {
    EmailSenderProvider emailSenderProviderProvider = session.getProvider(EmailSenderProvider.class);
    return new EEAEmailEventListenerProvider(session, emailSenderProviderProvider);
  }

  @Override
  public void init(Config.Scope config) {

  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {


  }

  @Override
  public void close() {
  }

  @Override
  public String getId() {
    return "email";
  }

}
