FROM openjdk:21-ea-1-jdk-oracle

WORKDIR /app

COPY target/api-gateway.jar api-gateway.jar

EXPOSE 9191

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]