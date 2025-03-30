
FROM openjdk:21-slim
ADD target/facsciences-planning-management.jar facsciences-planning-management.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/facsciences-planning-management.jar"]