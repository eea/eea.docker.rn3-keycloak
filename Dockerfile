FROM jboss/keycloak:5.0.0

ARG project_version=1.0

ADD target/EventEmailSender-$project_version.jar /opt/jboss/keycloak/standalone/deployments/EventEmailSender-$project_version.jar
