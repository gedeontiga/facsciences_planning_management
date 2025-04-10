# ---- Step 1: Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21-slim AS build
WORKDIR /app

# Copy only the POM file first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies separately to improve caching
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# ---- Step 2: Run Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the generated JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run with performance optimization flags
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]