# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy gradle files for caching
COPY gradlew .
RUN chmod +x gradlew
COPY gradle/ ./gradle/
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (optional but speeds up builds)
RUN ./gradlew dependencies --no-daemon

# Copy source and build
COPY src src
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar
COPY --from=build /app/build/libs/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=sit

# Expose the default port (Render will override this with PORT env var)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
