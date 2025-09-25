FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY target/jenkins-cicd-k8s-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
