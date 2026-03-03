FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/TrustAnchor-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xms512m"

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]