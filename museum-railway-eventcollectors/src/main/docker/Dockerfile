FROM docker.io/eclipse-temurin:21-alpine

COPY build/libs/museum-railway-events-eventcollectors.jar /opt/museum-railway-events-eventcollectors/museum-railway-events-eventcollectors.jar
WORKDIR /opt/museum-railway-events-eventcollectors/
EXPOSE 8083
CMD ["java", "-jar", "museum-railway-events-eventcollectors.jar"]