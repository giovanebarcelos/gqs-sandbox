# TechStore - API de carrinho em Java (Aula 24).
# Build em dois estagios: o Maven compila dentro do container (nao precisa de
# JDK na maquina para subir o ambiente) e a imagem final leva so' o JRE.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src/ src/
RUN mvn -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /srv
COPY --from=build /build/target/techstore-api.jar app.jar
RUN useradd --create-home techstore
USER techstore
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
