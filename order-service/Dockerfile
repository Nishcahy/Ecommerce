FROM openjdk:21

WORKDIR /app

COPY /target/order-service.jar order-service.jar

EXPOSE 9089

ENTRYPOINT [ "java","-jar","order-service.jar" ]