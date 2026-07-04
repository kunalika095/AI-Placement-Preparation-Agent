# ===================================================================
# Stage 1: Build stage
# ===================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy the pom.xml and download project dependencies (cached layers)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application package
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===================================================================
# Stage 2: Production JRE Runtime stage
# ===================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add a non-root system user for security purposes
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built jar from the build stage
COPY --from=builder /app/target/preparation-agent-0.0.1-SNAPSHOT.jar app.jar

# Expose server port
EXPOSE 8080

# Environment variables with sensible defaults
ENV MONGODB_URI=mongodb://localhost:27017/placement_prep
ENV GEMINI_API_KEY=""

# Health check to monitor container status
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]
