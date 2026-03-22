FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/identity-service-0.0.1-SNAPSHOT.jar identity-service.jar
EXPOSE 9898
ENTRYPOINT ["java", "-jar", "identity-service.jar"]