FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY target/NumberGenerator-1.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
