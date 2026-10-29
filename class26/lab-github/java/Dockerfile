# Boletim API em Java (Aula 26). Imagem publicada no GitHub Packages (GHCR)
# pelo workflow release.yml; localmente, "docker compose up --build".
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src/ src/
RUN mvn -q package -DskipTests -Djacoco.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /srv
COPY --from=build /build/target/boletim-api.jar app.jar
RUN useradd --create-home boletim
USER boletim
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
