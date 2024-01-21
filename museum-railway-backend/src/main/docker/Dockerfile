FROM docker.io/eclipse-temurin:21-alpine

EXPOSE 8080

COPY build/libs/museum-railway-events-backend*.jar /opt/museum-railway-events/museum-railway-events-backend.jar
WORKDIR /opt/museum-railway-events/
CMD ["java", "-Dserver.port=8080", "-jar", "museum-railway-events-backend.jar"]