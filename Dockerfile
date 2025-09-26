# Multi-arch base image (Eclipse Temurin provides both amd64 and arm64)
FROM alpine:latest

# Update the repository of OS and add bash to it
RUN apk update && apk add bash openjdk17-jre

# Set working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/jenkins-cicd-k8s-1.0.0.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
