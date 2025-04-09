FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]