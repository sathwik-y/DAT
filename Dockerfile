# Use lightweight Java runtime
FROM openjdk:21-jdk-slim

# Copy the JAR into the image
COPY target/bet-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java","-jar","/app.jar"]
