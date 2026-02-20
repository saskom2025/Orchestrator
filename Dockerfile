# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy everything
COPY . .

# Ensure gradlew is executable
RUN chmod +x gradlew

# Diagnostic: find the jar
RUN find . -name "gradle-wrapper.jar"

# Download dependencies (optional but speeds up builds)
RUN ./gradlew dependencies --no-daemon

# Build
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
