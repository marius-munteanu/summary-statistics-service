FROM openjdk:8-jre-slim
WORKDIR /app
COPY target/summary-statistics-1.0-SNAPSHOT.jar /app/summary-statistics-1.0-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "summary-statistics-1.0-SNAPSHOT.jar"]