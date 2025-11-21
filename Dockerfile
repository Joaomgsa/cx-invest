# Multi-stage Dockerfile: build with Maven (Java 21), run with JRE + sqlite3
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
# copy mvnw and .mvn if present to speed builds (optional)
COPY .mvn .mvn
COPY mvnw mvnw
RUN chmod +x mvnw || true
COPY src ./src

# build and normalize artifact name to /workspace/app.jar (first matching artifact)
RUN mvn -DskipTests package -Dquarkus.package.type=uber-jar && \
    ART=$(bash -lc "ls target/*-runner.jar target/quarkus-app/quarkus-run.jar target/*.jar 2>/dev/null | head -n1") && \
    if [ -n "$ART" ]; then cp "$ART" /workspace/app.jar; else echo "No artifact found in target/"; exit 1; fi

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y sqlite3 ca-certificates curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
