FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .

ARG JAVA_TOOL_OPTIONS="-XX:UseSVE=0"
ENV JAVA_TOOL_OPTIONS=$JAVA_TOOL_OPTIONS

RUN printenv & mvn clean package -DskipTests


# Runtime stage
FROM --platform=linux/arm64 amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/repotrack-1.0.0-SNAPSHOT.jar ./app.jar

# Run as non-root user
RUN adduser -D appuser && chown -R appuser:appuser /app
RUN mkdir -p /app/repositories && chown -R appuser:appuser /app/repositories

USER appuser

CMD ["java", "-jar", "app.jar"]