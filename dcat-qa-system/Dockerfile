FROM openjdk:11-jre-slim
WORKDIR /
COPY target/dcat-qa-system-0.0.1-SNAPSHOT.jar app.jar
CMD mkdir -p /qa/data
COPY src/data qa/data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]