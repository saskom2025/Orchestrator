# Build stage using a formal Gradle image to avoid wrapper issues
FROM gradle:jdk21 AS build
WORKDIR /home/gradle/src

# Copy the project files
# We use --chown=gradle:gradle because the gradle image runs as the 'gradle' user
COPY --chown=gradle:gradle . .

# Build the application
# We use 'gradle' instead of './gradlew'
RUN gradle bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
# The path in the gradle image is /home/gradle/src/build/libs/
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=sit

# Expose the default port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
