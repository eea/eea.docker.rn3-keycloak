package org.eea.keycloak.event.mail;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.ClientModel;
import org.keycloak.models.Constants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.RealmManager;

public class EEAEmailEventListenerProvider implements EventListenerProvider {

  private static final Logger log = Logger.getLogger(EEAEmailEventListenerProvider.class);
  private KeycloakSession session;
  private RealmProvider model;
  private EmailSenderProvider emailSenderProvider;
  private Set<EventType> includedEvents;

  public EEAEmailEventListenerProvider(KeycloakSession session,
      EmailSenderProvider emailSenderProvider, Set<EventType> includedEvents) {
    this.session = session;
    this.model = session.realms();
    this.emailSenderProvider = emailSenderProvider;
    this.includedEvents = includedEvents;
  }

  public EEAEmailEventListenerProvider(KeycloakSession session,
      EmailSenderProvider emailSenderProvider) {
    this.session = session;
    this.model = session.realms();
    this.emailSenderProvider = emailSenderProvider;
    this.includedEvents = new HashSet<>();
  }

  public void onEvent(Event event) {

    //A new user has been registered via Identity Provider. The account didn't exist before
     if(EventType.REGISTER.equals(event.getType())) {

    //Get Master Ream to retrieve admin user as the target email is helpdesk email, meaning, admin user
    RealmModel adminRealm = session.realms().getRealm("master");
    UserModel adminUser = session.users().getUserByUsername("admin", adminRealm);
    RealmModel realm = this.model.getRealm(event.getRealmId());
    UserModel user = this.session.users().getUserById(event.getUserId(), realm);
      try {
        this.emailSenderProvider.send(realm.getSmtpConfig(), adminUser,
            "New user registered on Keycloak",
            getEventBody(event, session, user),
            null);
        log.info("Sending email to "+adminUser.getEmail());
      } catch (EmailException var5) {
        log.error("Failed to send mail due to error {}", var5.getMessage(), var5);
      }
     }
  }

  public void onEvent(AdminEvent event, boolean includeRepresentation) {

  }

  private String getEventBody(Event event, KeycloakSession session, UserModel user) {
    StringBuilder sb = new StringBuilder("Hi,\n")
        .append("A new user has been created on keycloak:\nfirst name: ")
        .append(user.getFirstName()).append("\nlast name: ").append(user.getLastName())
        .append("\nuserId: ").append(user.getId()).append("\nuserMail: ").append(user.getEmail());

    sb.append("\nUser url: ")
        .append("http://kvm-rn3prod-01.pdmz.eea:31055/auth/admin/master/console/#/realms/Reportnet/users/").append(user.getId());
    return sb.toString();
  }



  public void close() {
  }

}
