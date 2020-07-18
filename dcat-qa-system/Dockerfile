FROM openjdk:11-jre-slim
WORKDIR /
COPY target/*.jar app.jar
CMD mkdir -p /qa/data
COPY src/data qa/data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]