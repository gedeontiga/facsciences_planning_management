FROM openjdk:17
WORKDIR /app
COPY target/facsciences-planning-management.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
