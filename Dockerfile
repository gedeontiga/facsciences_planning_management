# ---- Step 1: Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ---- Step 2: Run Stage ----
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
