FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/main-service-0.0.1-SNAPSHOT.jar main-service.jar
ENTRYPOINT ["java", "-jar", "main-service.jar"]