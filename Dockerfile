# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install netcat for healthcheck
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# Copy JAR from build stage
COPY --from=build /app/target/bet-0.0.1-SNAPSHOT.jar app.jar

# Healthcheck for DB connectivity
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD nc -z aws-0-ap-south-1.pooler.supabase.com 5432 || exit 1

# Run app with container-friendly JVM settings
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
