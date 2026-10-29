# LojaOnline - API de frete em Java (Aula 25).
# O pipeline ja' rodou os testes antes do "docker build"; aqui so' empacota.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src/ src/
RUN mvn -q package -DskipTests -Djacoco.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /srv
COPY --from=build /build/target/frete-api.jar app.jar
RUN useradd --create-home frete
USER frete
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
