FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia pom e baixa dependências em cache
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Copia código fonte e empacota (gera target/quarkus-app)
COPY src ./src
RUN mvn -B -DskipTests package

# Stage runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o diretório quarkus-app gerado
COPY --from=build /workspace/target/quarkus-app /app/quarkus-app

# Diretório para o arquivo sqlite (persistido via volume)
RUN mkdir -p /data
VOLUME ["/data"]

EXPOSE 8080

# Comando padrão para rodar o Quarkus em modo JVM
CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
