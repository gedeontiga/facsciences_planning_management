# ---- Step 1: Build Stage ----
FROM openjdk:21
WORKDIR /app

# Copy the application source code
COPY target/facsciences-planning-management.jar app.jar

# Copy the built application from the build stage
# COPY --from=build /app/app.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
