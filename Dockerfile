# First stage: Build the application
FROM bellsoft/liberica-openjdk-alpine:17 AS build
WORKDIR /app

# Copy source code
COPY . .
ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

# Build the application using Gradle
RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon --stacktrace --info -Dorg.gradle.vfs.watch=false

# Uncomment if using Maven instead
# RUN ./mvnw clean package -DskipTests

# Second stage: Run the application
FROM bellsoft/liberica-openjdk-alpine:17

# Set working directory
WORKDIR /app

# Copy only the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
