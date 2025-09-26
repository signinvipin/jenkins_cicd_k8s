# Multi-arch base image (Eclipse Temurin provides both amd64 and arm64)
FROM eclipse-temurin:17-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/jenkins-cicd-k8s-1.0.0.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
