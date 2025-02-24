FROM openjdk:17-jdk-alpine
LABEL authors="John Luke"
USER root
WORKDIR /app
COPY build/libs/config-0.0.1-SNAPSHOT.war /app/LiveConfig.war

USER 1001

ENTRYPOINT ["java", "-jar", "/app/LiveConfig.war"]
