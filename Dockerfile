FROM openjdk:22-slim
WORKDIR /app
COPY target/soccermatchstatsapi-0.0.1-SNAPSHOT.jar /app/soccermatchstatsapi.jar
EXPOSE 8080
CMD ["java", "-jar", "soccermatchstatsapi.jar"]
